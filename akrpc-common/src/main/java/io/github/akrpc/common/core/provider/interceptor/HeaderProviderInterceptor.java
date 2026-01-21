package io.github.akrpc.common.core.provider.interceptor;

import io.github.akrpc.common.constant.MagicValue;
import io.github.akrpc.common.dto.RpcRequestHeader;
import io.github.akrpc.common.dto.RpcRequestPacket;
import io.github.akrpc.common.dto.RpcResponse;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * 校验 RPC 请求头
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/13
 */
public class HeaderProviderInterceptor implements ProviderInterceptor {

    @Override
    public boolean process(RpcRequestPacket reqPacket, RpcResponse rpcRes, Map<String, Object> context) {
        RpcRequestHeader header = reqPacket.getHeader();

        if (header.getMagic().length != MagicValue.MAGIC_WORD.length) {
            rpcRes.error("无效的请求头: 魔法字长度错误");
            return false;
        }
        for (int i = 0; i < MagicValue.MAGIC_WORD.length; i++) {
            if (header.getMagic()[i] != MagicValue.MAGIC_WORD[i]) {
                rpcRes.error("无效的请求头: 魔法字错误");
                return false;
            }
        }

        if (StringUtils.isEmpty(header.getRequestId())) {
            rpcRes.error("无效的请求头: 请求ID不能为空");
            return false;
        }

        return true;
    }

}
