package com.shift.akrpc.consumer.config;

import com.shift.akrpc.common.config.RpcConsumerProperties;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 配置
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/6
 */
@Configuration
@Role(value = BeanDefinition.ROLE_INFRASTRUCTURE)
public class RestTemplateConfig {

    private final RpcConsumerProperties rpcConsumerProperties;

    public RestTemplateConfig(RpcConsumerProperties rpcConsumerProperties) {
        this.rpcConsumerProperties = rpcConsumerProperties;
    }

    @Bean
    @Role(value = BeanDefinition.ROLE_INFRASTRUCTURE)
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }

    @Bean
    @Role(value = BeanDefinition.ROLE_INFRASTRUCTURE)
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // 设置读取超时时间
        factory.setReadTimeout(rpcConsumerProperties.getReadTimeout());
        // 设置连接超时时间
        factory.setConnectTimeout(rpcConsumerProperties.getConnectTimeout());

        return factory;
    }
}
