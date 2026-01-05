package com.shift.akrpc.provider.config;

import com.shift.akrpc.common.annotation.RpcService;
import com.shift.akrpc.provider.registry.ServiceRegistry;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * 扫描并注册标记了 @RpcService 注解的服务类
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/5
 */
@Slf4j
@Component
public class RpcServiceScanner implements BeanPostProcessor {

    private final ServiceRegistry serviceRegistry;

    public RpcServiceScanner(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, @Nonnull String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        if (clazz.isAnnotationPresent(RpcService.class)) {
            RpcService rpcService = clazz.getAnnotation(RpcService.class);
            Class<?> interfaceClass = rpcService.value();
            String version = rpcService.version();

            serviceRegistry.register(interfaceClass.getName(), version, bean);
            log.info("RPC服务已注册: {}, version: {}", interfaceClass.getName(), version);
        }
        return bean;
    }
}
