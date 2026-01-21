package io.github.akrpc.common.core.discovery;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.health.HealthServicesRequest;
import com.ecwid.consul.v1.health.model.HealthService;
import io.github.akrpc.common.dto.RpcProvider;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 基于 Consul 的服务发现实现
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/7
 */
public class ConsulServiceDiscovery implements ServiceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(ConsulServiceDiscovery.class);

    private final ConsulClient consulClient;
    private final ScheduledExecutorService scheduler;
    private final Map<String, List<RpcProvider>> serviceCache;
    private final Map<String, String> serviceIdMap;
    private final Random random = new Random();

    // 配置参数
    private final long healthCheckInterval;
    private final int healthCheckPort;
    private final String healthCheckPath;
    private final String healthCheckProtocol;

    /**
     * 构造函数
     * @param consulHost Consul 服务器地址
     * @param consulPort Consul 服务器端口
     */
    public ConsulServiceDiscovery(String consulHost, int consulPort) {
        this(consulHost, consulPort, 30, 8080, "/actuator/health", "http");
    }

    /**
     * 完整构造函数
     * @param consulHost Consul 服务器地址
     * @param consulPort Consul 服务器端口
     * @param healthCheckInterval 健康检查间隔（秒）
     * @param healthCheckPort 健康检查端口
     * @param healthCheckPath 健康检查路径
     * @param healthCheckProtocol 健康检查协议
     */
    public ConsulServiceDiscovery(String consulHost, int consulPort,
                                  long healthCheckInterval, int healthCheckPort,
                                  String healthCheckPath, String healthCheckProtocol) {
        this.consulClient = new ConsulClient(consulHost, consulPort);
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.serviceCache = new ConcurrentHashMap<>();
        this.serviceIdMap = new ConcurrentHashMap<>();

        this.healthCheckInterval = healthCheckInterval;
        this.healthCheckPort = healthCheckPort;
        this.healthCheckPath = healthCheckPath;
        this.healthCheckProtocol = healthCheckProtocol;

        // 启动服务缓存刷新任务
        startCacheRefreshTask();

        logger.info("ConsulServiceDiscovery initialized with host: {}, port: {}", consulHost, consulPort);
    }

    @Override
    public void register(String serviceName, RpcProvider provider) {
        if (serviceName == null || provider == null) {
            throw new IllegalArgumentException("Service name and provider cannot be null");
        }

        // 生成服务ID
        String serviceId = generateServiceId(serviceName, provider);

        // 创建Consul服务注册信息
        NewService newService = new NewService();
        newService.setId(serviceId);
        newService.setName(serviceName);
        newService.setAddress(provider.getAddress());
        newService.setPort(provider.getPort());

        // 设置标签（可选，用于扩展信息）
        List<String> tags = new ArrayList<>();
        tags.add("createTime=" + provider.getCreateTime());
        newService.setTags(tags);

        // 配置健康检查
        NewService.Check check = new NewService.Check();

        // 根据协议选择不同的健康检查方式
        if ("http".equalsIgnoreCase(healthCheckProtocol) || "https".equalsIgnoreCase(healthCheckProtocol)) {
            // HTTP健康检查
            String checkUrl = String.format("%s://%s:%d%s",
                    healthCheckProtocol,
                    provider.getAddress(),
                    healthCheckPort > 0 ? healthCheckPort : provider.getPort(),
                    healthCheckPath);
            check.setHttp(checkUrl);
            logger.debug("Setting HTTP health check for {}: {}", serviceId, checkUrl);
        } else {
            // TCP健康检查（默认）
            check.setTcp(String.format("%s:%d",
                    provider.getAddress(),
                    healthCheckPort > 0 ? healthCheckPort : provider.getPort()));
            logger.debug("Setting TCP health check for {}: {}:{}",
                    serviceId, provider.getAddress(), provider.getPort());
        }

        check.setInterval(healthCheckInterval + "s");
        check.setTimeout("5s");
        check.setDeregisterCriticalServiceAfter("1m");

        newService.setCheck(check);

        try {
            // 注册服务
            consulClient.agentServiceRegister(newService);

            // 保存服务ID映射
            serviceIdMap.put(getServiceKey(serviceName, provider), serviceId);

            logger.info("Successfully registered service: {} with ID: {}", serviceName, serviceId);

            // 更新缓存
            updateServiceCache(serviceName);

        } catch (Exception e) {
//            logger.error("Failed to register service: {}", serviceName, e);
            throw new RuntimeException("Failed to register service: " + serviceName, e);
        }
    }

    @Override
    public void deregister(String serviceName, RpcProvider provider) {
        if (serviceName == null || provider == null) {
            throw new IllegalArgumentException("Service name and provider cannot be null");
        }

        String serviceKey = getServiceKey(serviceName, provider);
        String serviceId = serviceIdMap.get(serviceKey);

        if (serviceId == null) {
            logger.warn("Service not found for deregistration: {}, {}", serviceName, provider);
            return;
        }

        try {
            // 注销服务
            consulClient.agentServiceDeregister(serviceId);

            // 移除服务ID映射
            serviceIdMap.remove(serviceKey);

            // 更新缓存
            updateServiceCache(serviceName);

            logger.info("Successfully deregistered service: {} with ID: {}", serviceName, serviceId);

        } catch (Exception e) {
//            logger.error("Failed to deregister service: {}", serviceName, e);
            throw new RuntimeException("Failed to deregister service: " + serviceName, e);
        }
    }

    @Override
    public List<RpcProvider> getAllInstance(String serviceName) {
        if (serviceName == null) {
            throw new IllegalArgumentException("Service name cannot be null");
        }

        // 优先从缓存获取
        List<RpcProvider> cachedProviders = serviceCache.get(serviceName);
        if (cachedProviders != null) {
            return new ArrayList<>(cachedProviders);
        }

        // 缓存中没有，从Consul查询
        return queryServiceFromConsul(serviceName);
    }

    @Override
    public RpcProvider getServiceInstance(String serviceName) {
        List<RpcProvider> providers = getAllInstance(serviceName);
        if (CollectionUtils.isEmpty(providers)) {
            logger.warn("No available instances for service: {}", serviceName);
            return null;
        }

        // 简单的随机负载均衡策略
        int index = random.nextInt(providers.size());
        RpcProvider selectedProvider = providers.get(index);

        logger.debug("Selected instance for service {}: {}:{}", serviceName,
                selectedProvider.getAddress(), selectedProvider.getPort());

        return selectedProvider;
    }

    /**
     * 从 Consul 查询服务实例
     */
    private List<RpcProvider> queryServiceFromConsul(String serviceName) {
        try {
            // 构建健康服务查询请求
            HealthServicesRequest request = HealthServicesRequest.newBuilder()
                    .setPassing(true)  // 只返回健康实例
                    .build();

            // 查询健康的服务实例
            List<HealthService> healthServices = consulClient.getHealthServices(serviceName, request).getValue();

            List<RpcProvider> providers = new ArrayList<>();
            long now = System.currentTimeMillis() / 1000;

            for (HealthService healthService : healthServices) {
                HealthService.Service service = healthService.getService();

                RpcProvider provider = RpcProvider.builder().
                        name(service.getService()).address(service.getAddress()).port(service.getPort()).
                        createTime(now).updateTime(now).
                        build();
                providers.add(provider);
            }

            // 更新缓存
            serviceCache.put(serviceName, new ArrayList<>(providers));

            logger.debug("Found {} healthy instances for service: {}", providers.size(), serviceName);

            return providers;

        } catch (Exception e) {
            logger.error("Failed to query service from Consul: {}", serviceName, e);
            return new ArrayList<>();
        }
    }

    /**
     * 生成服务ID
     */
    private String generateServiceId(String serviceName, RpcProvider provider) {
        return String.format("%s-%s-%d",
                serviceName,
                provider.getAddress().replace('.', '_'),
                provider.getPort());
    }

    /**
     * 获取服务键
     */
    private String getServiceKey(String serviceName, RpcProvider provider) {
        return String.format("%s@%s:%d", serviceName, provider.getAddress(), provider.getPort());
    }

    /**
     * 更新服务缓存
     */
    private void updateServiceCache(String serviceName) {
        scheduler.submit(() -> {
            try {
                queryServiceFromConsul(serviceName);
            } catch (Exception e) {
                logger.error("Failed to update cache for service: {}", serviceName, e);
            }
        });
    }

    /**
     * 启动缓存刷新任务
     */
    private void startCacheRefreshTask() {
        // 每30秒刷新一次缓存
        scheduler.scheduleAtFixedRate(() -> {
            try {
                refreshAllServiceCache();
            } catch (Exception e) {
                logger.error("Failed to refresh service cache", e);
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    /**
     * 刷新所有服务缓存
     */
    private void refreshAllServiceCache() {
        for (var serviceName : serviceCache.keySet()) {
            try {
                queryServiceFromConsul(serviceName);
            } catch (Exception e) {
                logger.error("Failed to refresh cache for service: {}", serviceName, e);
            }
        }
    }

    /**
     * 手动刷新指定服务的缓存
     */
    public void refreshServiceCache(String serviceName) {
        if (serviceName != null) {
            updateServiceCache(serviceName);
        }
    }

    /**
     * 获取所有注册的服务名称
     */
    public List<String> getAllRegisteredServices() {
        try {
            return new ArrayList<>(consulClient.getAgentServices().getValue().keySet());
        } catch (Exception e) {
            logger.error("Failed to get registered services", e);
            return new ArrayList<>();
        }
    }

    /**
     * 关闭资源
     */
    public void shutdown() {
        try {
            scheduler.shutdown();
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
            logger.info("ConsulServiceDiscovery shutdown completed");
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
            logger.warn("ConsulServiceDiscovery shutdown interrupted");
        }
    }

    /**
     * 获取Consul客户端（用于高级操作）
     */
    public ConsulClient getConsulClient() {
        return consulClient;
    }
}
