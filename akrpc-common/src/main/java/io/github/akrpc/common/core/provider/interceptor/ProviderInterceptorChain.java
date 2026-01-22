package io.github.akrpc.common.core.provider.interceptor;

import io.github.akrpc.common.constant.MagicValue;
import io.github.akrpc.common.dto.RpcRequestHeader;
import io.github.akrpc.common.dto.RpcRequestPacket;
import io.github.akrpc.common.dto.RpcResponse;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务提供者拦截器链
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/12
 */
public class ProviderInterceptorChain {

    private final List<ProviderInterceptor> interceptors = new ArrayList<>(MagicValue.SIXTEEN);

    /**
     * 添加过滤器
     */
    public void addInterceptor(ProviderInterceptor interceptor) {
        if (interceptor != null) {
            interceptors.add(interceptor);
        }
    }

    /**
     * 执行过滤器链
     */
    public RpcResponse process(RpcRequestPacket packet, RpcRequestHeader header) {
        RpcResponse response = new RpcResponse(header.getRequestId());
        if (CollectionUtils.isEmpty(interceptors)) {
            return response;
        }

        // 依次执行过滤器
        Map<String, Object> context = new HashMap<>();
        for (ProviderInterceptor interceptor : interceptors) {
            boolean shouldContinue = interceptor.process(packet, header, response, context);
            if (!shouldContinue) {
                break;
            }
        }
        return response;
    }

}
