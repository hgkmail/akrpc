package io.github.akrpc.common.enums;

import lombok.Getter;

/**
 * RPC 编码类型
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/11
 */
@Getter
public enum RpcEncodeType {
    JSON((byte) 1),
    JDK((byte) 2),
    MSGPACK((byte) 3),
    KRYO((byte) 4),
    HESSIAN((byte) 5);

    private final byte code;

    RpcEncodeType(byte code) {
        this.code = code;
    }

    public static RpcEncodeType fromCode(byte code) {
        for (RpcEncodeType type : RpcEncodeType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的RPC编码类型: " + code);
    }

    /**
     * 根据名称获取编码类型，忽略大小写
     */
    public static RpcEncodeType fromName(String name) {
        for (RpcEncodeType type : RpcEncodeType.values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的RPC编码类型: " + name);
    }

}
