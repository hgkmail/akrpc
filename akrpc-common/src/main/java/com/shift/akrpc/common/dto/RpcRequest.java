package com.shift.akrpc.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * RPC 请求对象
 *
 * @author Kim Huang
 * @version 1.0
 * @see RpcResponse
 * @since 2026/1/4
 */
@Getter
@Setter
public class RpcRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String requestId;
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
    private String version;

}
