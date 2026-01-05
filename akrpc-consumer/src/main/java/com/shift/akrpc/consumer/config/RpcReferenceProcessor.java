package com.shift.akrpc.consumer.config;

import com.shift.akrpc.common.annotation.RpcReference;
import com.shift.akrpc.common.exception.RpcProxyException;
import com.shift.akrpc.consumer.proxy.RpcProxyFactory;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    private final RpcConsumerProperties properties;

    public RpcReferenceProcessor(RestTemplate restTemplate, RpcConsumerProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, @Nonnull String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(RpcReference.class)) {
                RpcReference rpcReference = field.getAnnotation(RpcReference.class);

                // 获取服务提供者URL，如果注解中没有指定，则从配置中获取
                String providerUrl = StringUtils.isEmpty(rpcReference.url()) ?
                        properties.getProviderUrl() : rpcReference.url();

                RpcProxyFactory proxyFactory = new RpcProxyFactory(
                        providerUrl,
                        rpcReference.version(),
                        rpcReference.timeout(),
                        restTemplate
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
