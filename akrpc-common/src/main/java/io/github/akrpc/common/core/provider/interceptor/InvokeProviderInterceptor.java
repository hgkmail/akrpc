package io.github.akrpc.common.core.provider.interceptor;

import io.github.akrpc.common.core.provider.ServiceRegistry;
import io.github.akrpc.common.dto.RpcRequestBody;
import io.github.akrpc.common.dto.RpcRequestPacket;
import io.github.akrpc.common.dto.RpcResponse;
import io.github.akrpc.common.utils.ConvertUtils;
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
        try {
            RpcRequestBody requestBody = (RpcRequestBody) context.get("body");

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

            // 处理参数类型转换
            this.handleParameterType(requestBody, method);

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

    /**
     * 处理参数类型转换
     */
    private void handleParameterType(RpcRequestBody requestBody, Method method) {
        if (requestBody.getParameters() == null || requestBody.getParameters().length == 0) {
            return;
        }

        int paramLen = requestBody.getParameters().length;
        Object[] convertedParams = new Object[paramLen];

        Class<?>[] parameterTypes = method.getParameterTypes();

        for (int i = 0; i < paramLen; i++) {
            Object originParam = requestBody.getParameters()[i];
            Class<?> targetType = parameterTypes[i];

            // 判断类型是否匹配
            // isAssignableFrom: 判断 targetType 是否是 originParam 的父类或接口
            if (originParam == null || targetType.isAssignableFrom(originParam.getClass())) {
                convertedParams[i] = originParam;
            } else {
                // 否则进行类型转换
                convertedParams[i] = ConvertUtils.convert(originParam, targetType);
            }
        }

        requestBody.setParameters(convertedParams);
    }

}
