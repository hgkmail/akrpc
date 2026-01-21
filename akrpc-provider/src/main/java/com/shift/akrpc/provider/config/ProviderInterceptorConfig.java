package com.shift.akrpc.provider.config;

import io.github.akrpc.common.core.provider.ServiceRegistry;
import io.github.akrpc.common.core.provider.interceptor.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provider 端拦截器链配置
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/13
 */
@Configuration
public class ProviderInterceptorConfig {

    private final ServiceRegistry serviceRegistry;

    public ProviderInterceptorConfig(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Bean
    public ProviderInterceptorChain providerInterceptorChain() {
        ProviderInterceptorChain chain = new ProviderInterceptorChain();
        chain.addInterceptor(new PacketProviderInterceptor());
        chain.addInterceptor(new HeaderProviderInterceptor());
        chain.addInterceptor(new ChecksumProviderInterceptor());
        chain.addInterceptor(new UncompressProviderInterceptor());
        chain.addInterceptor(new BodyProviderInterceptor());
        chain.addInterceptor(new InvokeProviderInterceptor(serviceRegistry));
        return chain;
    }

}
