package com.shift.akrpc.consumer.proxy;

import com.shift.akrpc.common.core.discovery.ServiceDiscovery;
import com.shift.akrpc.common.dto.RpcRequest;
import com.shift.akrpc.common.dto.RpcResponse;
import com.shift.akrpc.common.exception.RpcCallException;
import com.shift.akrpc.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
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
@Slf4j
public class RpcProxyFactory implements InvocationHandler {

    private final String providerUrl;
    private final String providerName;
    private final String version;
    private final long timeout;

    private final RestTemplate restTemplate;
    private final ServiceDiscovery serviceDiscovery;

    public RpcProxyFactory(
            String providerUrl,
            String providerName,
            String version,
            long timeout,
            RestTemplate restTemplate,
            ServiceDiscovery serviceDiscovery
    ) {
        this.providerUrl = providerUrl;
        this.providerName = providerName;
        this.version = version;
        this.timeout = timeout;

        this.restTemplate = restTemplate;
        this.serviceDiscovery = serviceDiscovery;
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

        String rpcUrl = this.getRealUrl() + "/rpc/invoke";

        log.info("调用服务: {}.{}, version: {}, url: {}, request: {}",
                request.getClassName(), request.getMethodName(), request.getVersion(), rpcUrl, JsonUtils.toJson(request));
        long beginTime = System.currentTimeMillis();

        // 发送 HTTP 请求
        RpcResponse response = restTemplate.postForObject(rpcUrl, request, RpcResponse.class);

        log.info("服务调用完成: {}.{}, version: {}, response: {}, 耗时: {} ms",
                request.getClassName(), request.getMethodName(), request.getVersion(),
                JsonUtils.toJson(response), System.currentTimeMillis() - beginTime);

        if (response == null) {
            throw new RpcCallException("响应为空");
        }

        if (!response.isSuccess()) {
            throw new RpcCallException(response.getError());
        }

        return response.getResult();
    }

    /**
     * 获取真实的服务提供者 URL
     */
    private String getRealUrl() {
        // 如果配置了直连 URL，则使用直连 URL
        if (StringUtils.isNotEmpty(this.providerUrl)) {
            return providerUrl;
        }

        // 否则通过服务发现获取服务提供者地址
        if (StringUtils.isEmpty(this.providerName)) {
            throw new RpcCallException("未配置服务名称或服务提供者 URL");
        }

        var providers = serviceDiscovery.getService(this.providerName);
        if (CollectionUtils.isEmpty(providers)) {
            throw new RpcCallException("未找到服务提供者: " + this.providerName);
        }

        // 简单起见，取第一个提供者 TODO: 负载均衡
        var provider = providers.getFirst();
        return "http://%s:%d".formatted(provider.getAddress(), provider.getPort());
    }

}
