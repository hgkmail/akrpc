package com.shift.akrpc.provider.registry;

import com.shift.akrpc.common.constant.MagicValue;
import com.shift.akrpc.common.core.provider.ServiceRegistry;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Role;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存的服务注册中心
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/5
 */
@Component
@Role(value = BeanDefinition.ROLE_INFRASTRUCTURE)
public class MemoryServiceRegistry implements ServiceRegistry {

    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    /**
     * 注册服务
     */
    @Override
    public void register(String serviceName, String version, Object serviceBean) {
        String key = getServiceKey(serviceName, version);
        assert !serviceMap.containsKey(key) : "Service already exist: " + key;

        serviceMap.put(key, serviceBean);
    }

    /**
     * 获取服务
     */
    @Override
    public Object getService(String serviceName, String version) {
        String key = getServiceKey(serviceName, version);
        assert serviceMap.containsKey(key) : "Service not found: " + key;

        return serviceMap.get(key);
    }

    private String getServiceKey(String serviceName, String version) {
        return "%s%s%s".formatted(serviceName, MagicValue.COLON, version);
    }
}
