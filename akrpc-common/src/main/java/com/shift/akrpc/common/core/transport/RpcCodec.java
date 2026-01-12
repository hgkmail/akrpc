package com.shift.akrpc.common.core.transport;

import com.shift.akrpc.common.dto.RpcRequestBody;

/**
 * RPC 编解码器接口
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/11
 */
public interface RpcCodec {

    /**
     * 编码 RPC 请求体
     *
     * @param body RPC 请求体
     * @return 编码后的字节数组
     */
    byte[] encode(RpcRequestBody body);

    /**
     * 解码 RPC 请求体
     */
    RpcRequestBody decode(byte[] bytes);

}
