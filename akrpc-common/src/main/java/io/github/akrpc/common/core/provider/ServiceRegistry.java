package io.github.akrpc.common.core.provider;

/**
 * 服务注册中心接口
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/5
 */
public interface ServiceRegistry {

    /**
     * 注册服务
     */
    void register(String serviceName, String version, Object serviceBean);

    /**
     * 获取服务
     */
    Object getService(String serviceName, String version);

}
