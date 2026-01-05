package com.shift.akrpc.common.exception;

/**
 * RPC 代理异常
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/6
 */
public class RpcProxyException extends RuntimeException {
    public RpcProxyException(String message) {
        super("注入 RPC 代理失败: " + message);
    }

    public RpcProxyException(Throwable cause) {
        super("注入 RPC 代理失败", cause);
    }

    public RpcProxyException(String message, Throwable cause) {
        super("注入 RPC 代理失败: " + message, cause);
    }
}
