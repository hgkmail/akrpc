package com.shift.akrpc.common.core.transport;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.shift.akrpc.common.dto.RpcRequestBody;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Hessian RPC 编解码器
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/13
 */
public class HessianRpcCodec implements RpcCodec {

    @Override
    public byte[] encode(RpcRequestBody body) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Hessian2Output output = new Hessian2Output(baos);
            output.writeObject(body);
            output.flush();
            byte[] bytes = baos.toByteArray();
            output.close();
            return bytes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RpcRequestBody decode(byte[] bytes) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
            Hessian2Input input = new Hessian2Input(bais);
            RpcRequestBody body = (RpcRequestBody) input.readObject();
            input.close();
            return body;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
