package io.github.akrpc.common.core.discovery;

import io.github.akrpc.common.dto.RpcProvider;

import java.util.List;

/**
 * 服务发现接口
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/7
 */
public interface ServiceDiscovery {

    /**
     * 注册服务
     * 如果服务不存在，则创建新服务
     * 如果服务已存在，则更新服务提供者列表
     * 如果服务提供者已存在，则更新该提供者信息
     *
     * @param serviceName 服务名称
     * @param provider 服务提供者
     */
    void register(String serviceName, RpcProvider provider);

    /**
     * 注销服务
     * 如果服务提供者不存在，则不做任何操作
     * 如果服务只剩下一个提供者，则删除该服务
     */
    void deregister(String serviceName, RpcProvider provider);

    /**
     * 获取所有服务实例
     *
     * @param serviceName 服务名称
     * @return 服务提供者列表
     */
    List<RpcProvider> getAllInstance(String serviceName);

    /**
     * 获取服务实例，内置负载均衡策略
     *
     * @param serviceName 服务名称
     * @return 服务提供者实例
     */
    RpcProvider getServiceInstance(String serviceName);

}
