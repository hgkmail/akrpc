package com.shift.akrpc.provider.registry;

import com.shift.akrpc.common.constant.MagicValue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存的服务注册中心
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/5
 */
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
