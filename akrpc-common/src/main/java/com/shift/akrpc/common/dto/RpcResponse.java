package com.shift.akrpc.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * RPC 响应对象
 *
 * @author Kim Huang
 * @version 1.0
 * @see RpcRequest
 * @since 2026/1/5
 */
@Getter
@Setter
public class RpcResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String requestId;
    private Object result;
    private String error;
    private boolean success;

}
