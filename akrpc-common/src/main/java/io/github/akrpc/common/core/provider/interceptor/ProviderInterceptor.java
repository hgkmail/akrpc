package io.github.akrpc.common.core.provider.interceptor;

import io.github.akrpc.common.dto.RpcRequestPacket;
import io.github.akrpc.common.dto.RpcResponse;

import java.util.Map;

/**
 * 服务提供者拦截器接口
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/12
 */
public interface ProviderInterceptor {

    /**
     * 执行拦截逻辑
     * @param reqPacket RPC 请求包
     * @param rpcRes RPC 响应对象
     * @return 如果返回 true 则继续执行后续拦截器或处理逻辑，返回 false 则终止处理
     */
    boolean process(RpcRequestPacket reqPacket, RpcResponse rpcRes, Map<String, Object> context);

}
