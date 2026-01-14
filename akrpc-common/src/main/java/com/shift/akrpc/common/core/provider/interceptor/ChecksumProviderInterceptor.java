package com.shift.akrpc.common.core.provider.interceptor;

import com.shift.akrpc.common.dto.RpcRequestPacket;
import com.shift.akrpc.common.dto.RpcResponse;
import com.shift.akrpc.common.utils.CRC32Utils;
import com.shift.akrpc.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 校验 RPC 请求包的 checksum
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/13
 */
@Slf4j
public class ChecksumProviderInterceptor implements ProviderInterceptor {

    @Override
    public boolean process(RpcRequestPacket reqPacket, RpcResponse rpcRes, Map<String, Object> context) {
        // 校验 checksum
        long checksum = CRC32Utils.getValue(reqPacket.getBody());
        if (checksum != reqPacket.getChecksum()) {
            log.warn("收到损坏的RPC请求包: {}", JsonUtils.toJson(reqPacket));

            rpcRes.error("请求包校验失败");
            return false;
        }

        return true;
    }

}
