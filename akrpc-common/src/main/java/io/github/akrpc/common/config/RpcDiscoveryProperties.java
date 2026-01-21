package io.github.akrpc.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Role;

/**
 * Discovery 配置
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/7
 */
@Getter
@Setter
@Role(value = BeanDefinition.ROLE_INFRASTRUCTURE)
@ConfigurationProperties(prefix = "akrpc.discovery")
public class RpcDiscoveryProperties {

    private String type;
    private String address;

    /**
     * 健康检查间隔，单位：秒
     */
    private Integer healthCheckInterval = 10;

}
