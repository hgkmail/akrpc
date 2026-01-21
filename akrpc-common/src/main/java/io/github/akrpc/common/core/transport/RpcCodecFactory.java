package io.github.akrpc.common.core.transport;

import io.github.akrpc.common.enums.RpcEncodeType;

import java.util.HashMap;
import java.util.Map;

/**
 * RPC 编码器工厂
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/11
 */
public class RpcCodecFactory {

    private RpcCodecFactory() {}

    private static final Map<Byte, RpcCodec> codecMap = new HashMap<>();

    static {
        codecMap.put(RpcEncodeType.JSON.getCode(), new JsonRpcCodec());
        codecMap.put(RpcEncodeType.JDK.getCode(), new JdkRpcCodec());
        codecMap.put(RpcEncodeType.MSGPACK.getCode(), new MsgpackRpcCodec());
        codecMap.put(RpcEncodeType.KRYO.getCode(), new KryoRpcCodec());
        codecMap.put(RpcEncodeType.HESSIAN.getCode(), new HessianRpcCodec());
    }

    /**
     * 根据编码类型获取对应的 RPC 编码器
     */
    public static RpcCodec getCodec(byte encodeType) {
        return codecMap.get(encodeType);
    }

}
