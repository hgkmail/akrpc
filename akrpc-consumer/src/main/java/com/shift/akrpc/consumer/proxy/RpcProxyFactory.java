package com.shift.akrpc.consumer.proxy;

import io.github.akrpc.common.annotation.RpcReference;
import io.github.akrpc.common.config.RpcConsumerProperties;
import io.github.akrpc.common.core.discovery.ServiceDiscovery;
import io.github.akrpc.common.core.transport.RpcCodec;
import io.github.akrpc.common.core.transport.RpcCodecFactory;
import io.github.akrpc.common.dto.RpcRequestBody;
import io.github.akrpc.common.dto.RpcRequestHeader;
import io.github.akrpc.common.dto.RpcRequestPacket;
import io.github.akrpc.common.dto.RpcResponse;
import io.github.akrpc.common.enums.RpcEncodeType;
import io.github.akrpc.common.exception.RpcCallException;
import io.github.akrpc.common.utils.CRC32Utils;
import io.github.akrpc.common.utils.ConvertUtils;
import io.github.akrpc.common.utils.GZIPUtils;
import io.github.akrpc.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
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
    private final RpcConsumerProperties rpcConsumerProperties;

    public RpcProxyFactory(
            RpcReference rpcReference,
            RestTemplate restTemplate,
            ServiceDiscovery serviceDiscovery,
            RpcConsumerProperties rpcConsumerProperties
    ) {
        this.providerUrl = rpcReference.url();
        this.providerName = rpcReference.name();
        this.version = rpcReference.version();
        this.timeout = rpcReference.timeout();

        this.restTemplate = restTemplate;
        this.serviceDiscovery = serviceDiscovery;
        this.rpcConsumerProperties = rpcConsumerProperties;
    }

    @SuppressWarnings("unchecked")
    public <T> T createProxy(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 获取编码器
        RpcEncodeType encodeType = RpcEncodeType.fromName(rpcConsumerProperties.getEncode());
        RpcCodec rpcCodec = RpcCodecFactory.getCodec(encodeType.getCode());

        String rpcUrl = this.buildUrl();
        RpcRequestHeader reqHeader = this.buildHeader(encodeType);
        RpcRequestBody reqBody = this.buildBody(method, args);
        RpcRequestPacket packet = this.buildPacket(reqHeader, reqBody, rpcCodec);

        RpcResponse response = this.makeRemoteCall(rpcUrl, packet, reqBody);

        if (response == null) {
            throw new RpcCallException("响应为空");
        }
        if (!response.isSuccess()) {
            throw new RpcCallException(response.getError());
        }

        // 返回值类型转换
        this.handleReturnType(response, method);

        return response.getResult();
    }

    private void handleReturnType(RpcResponse response, Method method) {
        if (response.getResult() == null) {
            return;
        }

        Class<?> returnType = method.getReturnType();

        // 判断类型是否一致，不一致则进行转换
        if (!returnType.isAssignableFrom(response.getResult().getClass())) {
            response.setResult(ConvertUtils.convert(response.getResult(), returnType));
        }
    }

    /**
     * 执行远程调用
     */
    private @Nullable RpcResponse makeRemoteCall(String rpcUrl, RpcRequestPacket packet, RpcRequestBody reqBody) {
        log.info("调用服务: {}.{}, version: {}, url: {}, reqBody: {}",
                reqBody.getClassName(), reqBody.getMethodName(), reqBody.getVersion(), rpcUrl, JsonUtils.toJson(reqBody));
        long beginTime = System.currentTimeMillis();

        // 发送 HTTP 请求
        RpcResponse response = restTemplate.postForObject(rpcUrl, packet, RpcResponse.class);

        log.info("服务调用完成: {}.{}, version: {}, response: {}, 耗时: {} ms",
                reqBody.getClassName(), reqBody.getMethodName(), reqBody.getVersion(),
                JsonUtils.toJson(response), System.currentTimeMillis() - beginTime);
        return response;
    }

    /**
     * 构建 RPC 调用 URL
     */
    private @NonNull String buildUrl() {
        return this.getRealAddress() + "/rpc/invoke";
    }

    /**
     * 构建 RPC 请求包
     */
    private @NonNull RpcRequestPacket buildPacket(
            RpcRequestHeader reqHeader,
            RpcRequestBody reqBody,
            RpcCodec rpcCodec
    ) {
        RpcRequestPacket packet = new RpcRequestPacket();
        packet.setHeader(reqHeader);
        packet.setBody(rpcCodec.encode(reqBody));

        // 判断是否启用 GZIP 压缩
        packet.getHeader().setGzip(ConvertUtils.bool2Byte(rpcConsumerProperties.isGzip()));
        if (rpcConsumerProperties.isGzip()) {
            packet.setBody(GZIPUtils.compress(packet.getBody()));
        }

        // 计算并设置 checksum
        packet.setChecksum(CRC32Utils.getValue(packet.getBody()));
        return packet;
    }

    /**
     * 构建 RPC 请求体
     */
    private RpcRequestBody buildBody(Method method, Object[] args) {
        // 构建 RPC 请求体
        RpcRequestBody requestBody = new RpcRequestBody();
        requestBody.setClassName(method.getDeclaringClass().getName());
        requestBody.setMethodName(method.getName());
        requestBody.setParameterTypes(method.getParameterTypes());
        requestBody.setParameters(args);
        requestBody.setReturnType(method.getReturnType());
        requestBody.setVersion(version);
        return requestBody;
    }

    /**
     * 构建 RPC 请求头
     */
    private RpcRequestHeader buildHeader(RpcEncodeType encodeType) {
        RpcRequestHeader header = new RpcRequestHeader();
        header.setRequestId(UUID.randomUUID().toString());
        header.setEncode(encodeType.getCode());
        return header;
    }

    /**
     * 获取真实的服务提供者地址
     */
    private String getRealAddress() {
        // 如果配置了直连 URL，则使用直连 URL
        if (StringUtils.isNotEmpty(this.providerUrl)) {
            return providerUrl;
        }

        // 否则通过服务发现获取服务提供者地址
        if (StringUtils.isEmpty(this.providerName)) {
            throw new RpcCallException("未配置服务名称或服务提供者 URL");
        }

        var provider = serviceDiscovery.getServiceInstance(this.providerName);
        if (provider == null) {
            throw new RpcCallException("未找到服务提供者: " + this.providerName);
        }

        return "http://%s:%d".formatted(provider.getAddress(), provider.getPort());
    }

}
