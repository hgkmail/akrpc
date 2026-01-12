package com.shift.akrpc.consumer.proxy;

import com.shift.akrpc.common.annotation.RpcReference;
import com.shift.akrpc.common.config.RpcConsumerProperties;
import com.shift.akrpc.common.core.discovery.ServiceDiscovery;
import com.shift.akrpc.common.core.transport.RpcCodec;
import com.shift.akrpc.common.core.transport.RpcCodecFactory;
import com.shift.akrpc.common.dto.RpcRequestBody;
import com.shift.akrpc.common.dto.RpcRequestHeader;
import com.shift.akrpc.common.dto.RpcRequestPacket;
import com.shift.akrpc.common.dto.RpcResponse;
import com.shift.akrpc.common.enums.RpcEncodeType;
import com.shift.akrpc.common.exception.RpcCallException;
import com.shift.akrpc.common.utils.CRC32Utils;
import com.shift.akrpc.common.utils.GZIPUtils;
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
        // 构建 RPC 请求
        RpcRequestBody requestBody = new RpcRequestBody();
        requestBody.setClassName(method.getDeclaringClass().getName());
        requestBody.setMethodName(method.getName());
        requestBody.setParameterTypes(method.getParameterTypes());
        requestBody.setParameters(args);
        requestBody.setVersion(version);

        // 获取编码器
        RpcEncodeType encodeType = RpcEncodeType.fromName(rpcConsumerProperties.getEncode());
        RpcCodec rpcCodec = RpcCodecFactory.getCodec(encodeType.getCode());

        RpcRequestHeader header = new RpcRequestHeader();
        header.setRequestId(UUID.randomUUID().toString());
        header.setEncode(encodeType.getCode());
        RpcRequestPacket packet = new RpcRequestPacket();
        packet.setHeader(header);
        packet.setBody(rpcCodec.encode(requestBody));
        // 判断是否启用 GZIP 压缩
        packet.getHeader().setGzip( rpcConsumerProperties.isGzip() ? (byte) 1 : (byte) 0);
        if (rpcConsumerProperties.isGzip()) {
            packet.setBody(GZIPUtils.compress(packet.getBody()));
        }
        // 计算并设置 checksum
        packet.setChecksum(CRC32Utils.getValue(packet.getBody()));

        String rpcUrl = this.getRealUrl() + "/rpc/invoke";

        log.info("调用服务: {}.{}, version: {}, url: {}, requestBody: {}",
                requestBody.getClassName(), requestBody.getMethodName(), requestBody.getVersion(), rpcUrl, JsonUtils.toJson(requestBody));
        long beginTime = System.currentTimeMillis();

        // 发送 HTTP 请求
        RpcResponse response = restTemplate.postForObject(rpcUrl, packet, RpcResponse.class);

        log.info("服务调用完成: {}.{}, version: {}, response: {}, 耗时: {} ms",
                requestBody.getClassName(), requestBody.getMethodName(), requestBody.getVersion(),
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
