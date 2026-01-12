package com.shift.akrpc.common.core.transport;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.shift.akrpc.common.dto.RpcRequestBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Kryo 编码解码器
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/13
 */
public class KryoRpcCodec implements RpcCodec {

    private final Kryo kryo;

    public KryoRpcCodec() {
        this.kryo = new Kryo();
        this.kryo.setRegistrationRequired(false);
    }

    @Override
    public byte[] encode(RpcRequestBody body) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             Output output = new Output(baos)
        ) {
            kryo.writeObject(output, body);
            output.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RpcRequestBody decode(byte[] bytes) {
        try (Input input = new Input(bytes)) {
            return kryo.readObject(input, RpcRequestBody.class);
        }
    }
}
