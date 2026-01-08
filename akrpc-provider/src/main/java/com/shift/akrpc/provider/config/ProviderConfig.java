package com.shift.akrpc.provider.config;

import com.shift.akrpc.common.core.discovery.ServiceDiscovery;
import com.shift.akrpc.common.dto.RpcProvider;
import com.shift.akrpc.common.utils.InetUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 服务提供者配置类
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/9
 */
@Configuration
public class ProviderConfig implements InitializingBean, DisposableBean {

    @Value("${server.port}")
    private int serverPort;

    @Value("${spring.application.name}")
    private String applicationName;

    private final InetUtils inetUtils;

    private final ServiceDiscovery serviceDiscovery;

    private RpcProvider registeredProvider;

    public ProviderConfig(InetUtils inetUtils, ServiceDiscovery serviceDiscovery) {
        this.inetUtils = inetUtils;
        this.serviceDiscovery = serviceDiscovery;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String ipAddress = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();

        RpcProvider rpcProvider = new RpcProvider();
        rpcProvider.setAddress(ipAddress);
        rpcProvider.setPort(serverPort);
        long currentTime = System.currentTimeMillis() / 1000;
        rpcProvider.setCreateTime(currentTime);
        rpcProvider.setUpdateTime(currentTime);
        serviceDiscovery.register(applicationName, rpcProvider);

        registeredProvider = rpcProvider;
    }

    @Override
    public void destroy() throws Exception {
        if (registeredProvider != null) {
            serviceDiscovery.deregister(applicationName, registeredProvider);
        }
    }
}
