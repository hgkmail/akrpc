package com.shift.akrpc.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * RPC 请求头
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/11
 */
@Getter
@Setter
public class RpcRequestHeader implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 校验头
     */
    private char[] magic = new char[]{'A', 'K', 'R', 'P', 'C'};

    /**
     * 结构体版本号
     */
    private byte ver = 1;

    /**
     * 编码类型 1-JSON 2-JDK自带序列化 3-msgpack 4-kryo 5-hessian
     */
    private byte encode = 1;

    /**
     * 是否启用gzip压缩 0-不压缩 1-压缩
     */
    private byte gzip = 0;

    /**
     * 请求唯一标识
     */
    private String requestId;
}
