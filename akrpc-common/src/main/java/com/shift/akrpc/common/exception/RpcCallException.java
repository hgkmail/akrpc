package com.shift.akrpc.common.exception;

/**
 * RPC 调用异常
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/6
 */
public class RpcCallException extends RuntimeException {
    public RpcCallException(String message) {
        super("RPC 调用失败: " + message);
    }

    public RpcCallException(String message, Throwable cause) {
        super("RPC 调用失败: " + message, cause);
    }
}
