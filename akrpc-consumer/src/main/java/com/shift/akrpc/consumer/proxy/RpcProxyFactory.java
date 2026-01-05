package com.shift.akrpc.consumer.proxy;

import com.shift.akrpc.common.dto.RpcRequest;
import com.shift.akrpc.common.dto.RpcResponse;
import com.shift.akrpc.common.exception.RpcCallException;
import org.springframework.web.client.RestTemplate;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * RPC 动态代理工厂
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/6
 */
public class RpcProxyFactory implements InvocationHandler {

    private final String providerUrl;
    private final String version;
    private final long timeout;
    private final RestTemplate restTemplate;

    public RpcProxyFactory(String providerUrl, String version, long timeout, RestTemplate restTemplate) {
        this.providerUrl = providerUrl;
        this.version = version;
        this.timeout = timeout;
        this.restTemplate = restTemplate;
    }

    @SuppressWarnings("unchecked")
    public <T> T createProxy(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 构建 RPC 请求
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        request.setVersion(version);

        // 发送 HTTP 请求
        String url = providerUrl + "/rpc/invoke";
        RpcResponse response = restTemplate.postForObject(url, request, RpcResponse.class);

        if (response == null) {
            throw new RpcCallException("响应为空");
        }

        if (!response.isSuccess()) {
            throw new RpcCallException(response.getError());
        }

        return response.getResult();
    }
}
