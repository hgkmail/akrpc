package io.github.akrpc.common.core.provider.interceptor;

import io.github.akrpc.common.dto.RpcRequestHeader;
import io.github.akrpc.common.dto.RpcRequestPacket;
import io.github.akrpc.common.dto.RpcResponse;
import io.github.akrpc.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 校验 RPC 请求包的基本完整性
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/12
 */
@Slf4j
public class PacketProviderInterceptor implements ProviderInterceptor {

    @Override
    public boolean process(RpcRequestPacket reqPacket, RpcRequestHeader header, RpcResponse rpcRes,
                           Map<String, Object> context) {
        if (reqPacket == null ||
            header == null ||
            reqPacket.getBody() == null ||
            reqPacket.getChecksum() == 0
        ) {
            log.warn("收到无效的RPC请求包: {}", JsonUtils.toJson(reqPacket));
            rpcRes.error("无效的请求包");
            return false;
        }

        return true;
    }

}
