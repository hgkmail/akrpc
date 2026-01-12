package com.shift.akrpc.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Role;

/**
 * Consumer 配置
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/6
 */
@Getter
@Setter
@Role(value = BeanDefinition.ROLE_INFRASTRUCTURE)
@ConfigurationProperties(prefix = "akrpc.consumer")
public class RpcConsumerProperties {

    /**
     * 读取超时时间，单位毫秒
     */
    private Integer readTimeout = 5000;

    /**
     * 连接超时时间，单位毫秒
     */
    private Integer connectTimeout = 1000;

    /**
     * 重试次数
     */
    private Integer retries = 3;

    /**
     * 编码类型，不区分大小写，默认 json
     * @see com.shift.akrpc.common.enums.RpcEncodeType
     */
    private String encode = "json";

    /**
     * 是否启用 gzip 压缩
     */
    private boolean gzip = false;

}
