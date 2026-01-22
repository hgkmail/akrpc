package io.github.akrpc.common.core.provider.interceptor;

import io.github.akrpc.common.constant.MagicValue;
import io.github.akrpc.common.dto.RpcRequestHeader;
import io.github.akrpc.common.dto.RpcRequestPacket;
import io.github.akrpc.common.dto.RpcResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

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
    public boolean process(RpcRequestPacket reqPacket, RpcRequestHeader header, RpcResponse rpcRes, Map<String, Object> context) {
        if (!Strings.CI.equals(header.getMagic(), MagicValue.MAGIC_WORD)) {
            rpcRes.error("无效的请求头: 魔法字错误");
            return false;
        }

        if (StringUtils.isEmpty(header.getRequestId())) {
            rpcRes.error("无效的请求头: 请求ID不能为空");
            return false;
        }

        return true;
    }

}
