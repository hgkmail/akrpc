package com.shift.akrpc.consumer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Role;
import org.springframework.stereotype.Component;

/**
 * Consumer 配置
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/6
 */
@Getter
@Setter
@Component
@Role(value = BeanDefinition.ROLE_INFRASTRUCTURE)
@ConfigurationProperties(prefix = "akrpc.consumer")
public class RpcConsumerProperties {

    /**
     * 服务提供者地址
     */
    private String providerUrl = "http://localhost:8080";

    /**
     * 读取超时时间，单位毫秒
     */
    private int readTimeout = 5000;

    /**
     * 连接超时时间，单位毫秒
     */
    private int connectTimeout = 1000;

}
