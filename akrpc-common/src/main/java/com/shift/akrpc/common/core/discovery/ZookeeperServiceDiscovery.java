package com.shift.akrpc.common.core.discovery;

import com.shift.akrpc.common.dto.RpcProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于 ZooKeeper + Curator 的服务发现实现
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/16
 */
@Slf4j
public class ZookeeperServiceDiscovery implements ServiceDiscovery {

    /**
     * ZooKeeper 服务注册根路径
     */
    private static final String BASE_PATH = "/akrpc-services";

    /**
     * Curator 客户端
     */
    private final CuratorFramework curatorClient;

    /**
     * Curator 服务发现
     */
    private final org.apache.curator.x.discovery.ServiceDiscovery<RpcProvider> serviceDiscovery;

    /**
     * 服务提供者缓存，用于服务发现
     */
    private final Map<String, ServiceProvider<RpcProvider>> serviceProviderCache = new ConcurrentHashMap<>();

    /**
     * 构造函数
     *
     * @param zkConnectString ZooKeeper 连接字符串，如 "127.0.0.1:2181"
     */
    public ZookeeperServiceDiscovery(String zkConnectString) {
        // 创建 Curator 客户端
        this.curatorClient = CuratorFrameworkFactory.builder()
                .connectString(zkConnectString)
                .sessionTimeoutMs(60000)
                .connectionTimeoutMs(15000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();

        // 启动客户端
        this.curatorClient.start();
        log.info("Curator client started and connected to ZooKeeper at {}", zkConnectString);

        // 创建服务发现实例
        this.serviceDiscovery = ServiceDiscoveryBuilder.builder(RpcProvider.class)
                .client(curatorClient)
                .basePath(BASE_PATH)
                .serializer(new JsonInstanceSerializer<>(RpcProvider.class))
                .build();

        try {
            this.serviceDiscovery.start();
            log.info("Service discovery started at base path {}", BASE_PATH);
        } catch (Exception e) {
            throw new RuntimeException("Failed to start service discovery", e);
        }
    }

    @Override
    public void register(String serviceName, RpcProvider provider) {
        try {
            ServiceInstance<RpcProvider> serviceInstance = buildServiceInstance(serviceName, provider);
            serviceDiscovery.registerService(serviceInstance);

            log.info("Registered service: {} with instance ID: {}", serviceName, serviceInstance.getId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to register service: " + serviceName, e);
        }
    }

    @Override
    public void deregister(String serviceName, RpcProvider provider) {
        try {
            ServiceInstance<RpcProvider> serviceInstance = buildServiceInstance(serviceName, provider);
            serviceDiscovery.unregisterService(serviceInstance);

            log.info("Deregistered service: {} with instance ID: {}", serviceName, serviceInstance.getId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to deregister service: " + serviceName, e);
        }
    }

    @Override
    public List<RpcProvider> getAllInstance(String serviceName) {
        try {
            // 获取或创建 ServiceProvider
            ServiceProvider<RpcProvider> serviceProvider = getServiceProvider(serviceName);
            // 获取所有服务实例
            Collection<ServiceInstance<RpcProvider>> instances = serviceProvider.getAllInstances();

            List<RpcProvider> providers = new ArrayList<>();
            for (ServiceInstance<RpcProvider> instance : instances) {
                RpcProvider rpcProvider = instance.getPayload();
                if (rpcProvider != null) {
                    providers.add(rpcProvider);
                }
            }
            return providers;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get service: " + serviceName, e);
        }
    }

    @Override
    public RpcProvider getServiceInstance(String serviceName) {
        try {
            // 获取或创建 ServiceProvider
            ServiceProvider<RpcProvider> serviceProvider = getServiceProvider(serviceName);
            // 获取服务实例，默认是RoundRobin负载均衡策略
            ServiceInstance<RpcProvider> instance = serviceProvider.getInstance();

            if (instance == null) {
                return null;
            }
            return instance.getPayload();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get service: " + serviceName, e);
        }
    }

    @NotNull
    private ServiceProvider<RpcProvider> getServiceProvider(String serviceName) {
        ServiceProvider<RpcProvider> serviceProvider = serviceProviderCache.computeIfAbsent(
                serviceName,
                name -> {
                    ServiceProvider<RpcProvider> provider = serviceDiscovery.serviceProviderBuilder()
                            .serviceName(name)
                            .providerStrategy(new RoundRobinStrategy<>())
                            .build();
                    try {
                        provider.start();
                        log.info("Started service provider for service: {}", name);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to start service provider for: " + name, e);
                    }
                    return provider;
                }
        );
        log.info("Retrieved service provider for service: {}", serviceName);

        return serviceProvider;
    }

    /**
     * 构建 Curator ServiceInstance
     *
     * @param serviceName 服务名称
     * @param provider    RPC 提供者信息
     * @return ServiceInstance 实例
     */
    private ServiceInstance<RpcProvider> buildServiceInstance(String serviceName, RpcProvider provider) throws Exception {
        // 使用 name:address:port 作为实例 ID，确保唯一性
        String instanceId = "%s:%s:%d".formatted(provider.getName(), provider.getAddress(), provider.getPort());

        return ServiceInstance.<RpcProvider>builder()
                .id(instanceId)
                .name(serviceName)
                .address(provider.getAddress())
                .port(provider.getPort())
                .payload(provider)
                .build();
    }

    /**
     * 关闭服务发现
     */
    public void shutdown() {
        try {
            // 关闭服务发现，会自动关闭所有 ServiceProvider
            if (serviceDiscovery != null) {
                serviceDiscovery.close();
            }

            serviceProviderCache.clear();

            // 关闭 Curator 客户端
            if (curatorClient != null) {
                curatorClient.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to close service discovery", e);
        }
    }
}
