package com.shift.akrpc.common.core.provider.interceptor;

import com.shift.akrpc.common.core.provider.ServiceRegistry;
import com.shift.akrpc.common.dto.RpcRequestBody;
import com.shift.akrpc.common.dto.RpcRequestPacket;
import com.shift.akrpc.common.dto.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 反射调用指定服务
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/13
 */
@Slf4j
public class InvokeProviderInterceptor implements ProviderInterceptor {

    private ServiceRegistry serviceRegistry;

    public InvokeProviderInterceptor(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public boolean process(RpcRequestPacket reqPacket, RpcResponse rpcRes, Map<String, Object> context) {
        RpcRequestBody requestBody = (RpcRequestBody) context.get("body");

        try {
            Object service = serviceRegistry.getService(
                    requestBody.getClassName(),
                    requestBody.getVersion()
            );

            if (service == null) {
                rpcRes.setSuccess(false);
                rpcRes.setError("服务未找到: " + requestBody.getClassName());
                return false;
            }

            Method method = service.getClass().getMethod(
                    requestBody.getMethodName(),
                    requestBody.getParameterTypes()
            );

            Object result = method.invoke(service, requestBody.getParameters());

            rpcRes.success(result);
            return true;
        } catch (Exception e) {
            rpcRes.setSuccess(false);
            rpcRes.setError(e.getMessage());
            log.error("RPC调用出错: {}", e.getMessage(), e);
            return false;
        }

    }
}
