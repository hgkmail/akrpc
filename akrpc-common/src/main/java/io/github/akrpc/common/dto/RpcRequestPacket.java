package io.github.akrpc.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * RPC 请求包
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/11
 */
@Getter
@Setter
public class RpcRequestPacket implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 请求头
     */
    private RpcRequestHeader header;

    /**
     * 请求体
     * @see RpcRequestBody
     */
    private byte[] body;

    /**
     * body 的 crc32 校验码
     */
    private long checksum;
}
