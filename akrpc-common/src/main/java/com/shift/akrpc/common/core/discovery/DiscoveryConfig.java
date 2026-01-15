package com.shift.akrpc.common.core.discovery;

import com.shift.akrpc.common.config.InetUtilsProperties;
import com.shift.akrpc.common.config.RpcDiscoveryProperties;
import com.shift.akrpc.common.constant.MagicValue;
import com.shift.akrpc.common.dto.ServiceAddress;
import com.shift.akrpc.common.utils.CommonUtils;
import com.shift.akrpc.common.utils.InetUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * 服务发现配置类
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/8
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@EnableConfigurationProperties(value = {RpcDiscoveryProperties.class})
@Configuration(proxyBeanMethods = false)
public class DiscoveryConfig {

    @Value("${server.port}")
    private int serverPort;

    private final RpcDiscoveryProperties rpcDiscoveryProperties;

    public DiscoveryConfig(RpcDiscoveryProperties rpcDiscoveryProperties) {
        this.rpcDiscoveryProperties = rpcDiscoveryProperties;
    }

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnProperty(name = {"akrpc.discovery.type"}, havingValue = "consul")
    @Bean(destroyMethod = "shutdown")
    public ServiceDiscovery consulServiceDiscovery() {
        // 解析地址
        ServiceAddress consulAddress = CommonUtils.parseServiceAddress(
                rpcDiscoveryProperties.getAddress(),
                MagicValue.LOCALHOST,
                8500
        );

        return new ConsulServiceDiscovery(
                consulAddress.host(),
                consulAddress.port(),
                rpcDiscoveryProperties.getHealthCheckInterval(),
                serverPort,
                "/actuator/health",
                "http"
        );
    }

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnProperty(name = {"akrpc.discovery.type"}, havingValue = "zookeeper")
    @Bean(destroyMethod = "shutdown")
    public ServiceDiscovery zookeeperServiceDiscovery() {
        return new ZookeeperServiceDiscovery(rpcDiscoveryProperties.getAddress());
    }

    @Bean
    public InetUtils inetUtils() {
        return new InetUtils(new InetUtilsProperties());
    }

}
