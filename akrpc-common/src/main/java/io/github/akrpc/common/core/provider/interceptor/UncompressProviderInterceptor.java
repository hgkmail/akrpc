package io.github.akrpc.common.core.provider.interceptor;

import io.github.akrpc.common.dto.RpcRequestPacket;
import io.github.akrpc.common.dto.RpcResponse;
import io.github.akrpc.common.utils.GZIPUtils;

import java.util.Map;

/**
 * 解压 RPC 请求体
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/13
 */
public class UncompressProviderInterceptor implements ProviderInterceptor {

    @Override
    public boolean process(RpcRequestPacket reqPacket, RpcResponse rpcRes, Map<String, Object> context) {
        // 解压请求体
        if (reqPacket.getHeader().getGzip() == 1) {
            byte[] decompressedBody = GZIPUtils.decompress(reqPacket.getBody());
            reqPacket.setBody(decompressedBody);
        }

        return true;
    }
}
