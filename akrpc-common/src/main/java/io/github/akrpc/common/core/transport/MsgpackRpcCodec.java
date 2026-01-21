package io.github.akrpc.common.core.transport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.akrpc.common.dto.RpcRequestBody;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.IOException;

/**
 * Msgpack 编解码器
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/11
 */
public class MsgpackRpcCodec implements RpcCodec {

    private ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());

    @Override
    public byte[] encode(RpcRequestBody body) {
        try {
            return objectMapper.writeValueAsBytes(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RpcRequestBody decode(byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, RpcRequestBody.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
