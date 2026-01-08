package com.shift.akrpc.provider.config;

import com.google.common.base.Splitter;
import com.shift.akrpc.common.config.InetUtilsProperties;
import com.shift.akrpc.common.config.RpcDiscoveryProperties;
import com.shift.akrpc.common.constant.MagicValue;
import com.shift.akrpc.common.core.discovery.ConsulServiceDiscovery;
import com.shift.akrpc.common.core.discovery.ServiceDiscovery;
import com.shift.akrpc.common.utils.InetUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 服务发现配置类
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/8
 */
@Configuration
public class DiscoveryConfig {

    @Value("${server.port}")
    private int serverPort;

    private final RpcDiscoveryProperties rpcDiscoveryProperties;

    public DiscoveryConfig(RpcDiscoveryProperties rpcDiscoveryProperties) {
        this.rpcDiscoveryProperties = rpcDiscoveryProperties;
    }

    @ConditionalOnProperty(name = {"akrpc.discovery.type"}, havingValue = "consul")
    @Bean(destroyMethod = "shutdown")
    public ServiceDiscovery serviceDiscovery() {
        // 解析地址
        List<String> addressParts = Splitter.on(MagicValue.COLON).splitToList(rpcDiscoveryProperties.getAddress());
        String host = StringUtils.isNotBlank(addressParts.get(0)) ? addressParts.get(0) : "localhost";
        int port = addressParts.size() > 1 ? Integer.parseInt(addressParts.get(1)) : 8500;

        return new ConsulServiceDiscovery(
                host,
                port,
                30,
                serverPort,
                "/actuator/health",
                "http"
        );
    }

    @Bean
    public InetUtils inetUtils() {
        return new InetUtils(new InetUtilsProperties());
    }

}
