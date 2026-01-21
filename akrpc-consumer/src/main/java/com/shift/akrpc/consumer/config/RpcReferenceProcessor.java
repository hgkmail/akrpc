package com.shift.akrpc.consumer.config;

import io.github.akrpc.common.annotation.RpcReference;
import io.github.akrpc.common.config.RpcConsumerProperties;
import io.github.akrpc.common.core.discovery.ServiceDiscovery;
import io.github.akrpc.common.exception.RpcProxyException;
import com.shift.akrpc.consumer.proxy.RpcProxyFactory;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.lang.reflect.Field;

/**
 * 处理 @RpcReference 注解
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/6
 */
@Slf4j
@Component
public class RpcReferenceProcessor implements BeanPostProcessor {

    private final RestTemplate restTemplate;

    private final ServiceDiscovery serviceDiscovery;

    private final RpcConsumerProperties rpcConsumerProperties;

    public RpcReferenceProcessor(
            RestTemplate restTemplate,
            ServiceDiscovery serviceDiscovery,
            RpcConsumerProperties rpcConsumerProperties
    ) {
        this.restTemplate = restTemplate;
        this.serviceDiscovery = serviceDiscovery;
        this.rpcConsumerProperties = rpcConsumerProperties;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, @Nonnull String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(RpcReference.class)) {
                RpcReference rpcReference = field.getAnnotation(RpcReference.class);

                RpcProxyFactory proxyFactory = new RpcProxyFactory(
                        rpcReference,
                        restTemplate,
                        serviceDiscovery,
                        rpcConsumerProperties
                );

                Object proxy = proxyFactory.createProxy(field.getType());

                field.setAccessible(true);
                try {
                    field.set(bean, proxy);
                    log.info("RPC 代理已注入: {}", field.getType().getName());
                } catch (IllegalAccessException e) {
                    throw new RpcProxyException(e);
                }
            }
        }

        return bean;
    }

}
