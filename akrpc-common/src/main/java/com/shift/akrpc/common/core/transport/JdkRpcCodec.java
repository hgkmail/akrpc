package com.shift.akrpc.common.core.transport;

import com.shift.akrpc.common.dto.RpcRequestBody;
import org.apache.commons.lang3.SerializationUtils;

/**
 * JDK 序列化编解码器
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/11
 */
public class JdkRpcCodec implements RpcCodec {

    @Override
    public byte[] encode(RpcRequestBody body) {
        return SerializationUtils.serialize(body);
    }

    @Override
    public RpcRequestBody decode(byte[] bytes) {
        return SerializationUtils.deserialize(bytes);
    }

}
