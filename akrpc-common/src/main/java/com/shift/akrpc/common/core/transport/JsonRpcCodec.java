package com.shift.akrpc.common.core.transport;

import com.shift.akrpc.common.dto.RpcRequestBody;
import com.shift.akrpc.common.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * JSON RPC 编解码器
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/11
 */
public class JsonRpcCodec implements RpcCodec {

    @Override
    public byte[] encode(RpcRequestBody body) {
        String json = JsonUtils.toJson(body);
        if (StringUtils.isEmpty(json)) {
            throw new IllegalArgumentException("Failed to encode RPC request body: empty JSON");
        }

        return json.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public RpcRequestBody decode(byte[] bytes) {
        String json = new String(bytes, StandardCharsets.UTF_8);
        return JsonUtils.fromJson(json, RpcRequestBody.class);
    }

}
