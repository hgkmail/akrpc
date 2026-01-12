package com.shift.akrpc.common.core.provider.interceptor;

import com.shift.akrpc.common.constant.MagicValue;
import com.shift.akrpc.common.core.transport.RpcCodec;
import com.shift.akrpc.common.core.transport.RpcCodecFactory;
import com.shift.akrpc.common.dto.RpcRequestBody;
import com.shift.akrpc.common.dto.RpcRequestPacket;
import com.shift.akrpc.common.dto.RpcResponse;
import com.shift.akrpc.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * 请求体处理拦截器
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/13
 */
@Slf4j
public class BodyProviderInterceptor implements ProviderInterceptor {

    @Override
    public boolean process(RpcRequestPacket reqPacket, RpcResponse rpcRes, Map<String, Object> context) {
        // 获取编码器
        byte encodeType = reqPacket.getHeader().getEncode();
        RpcCodec rpcCodec = RpcCodecFactory.getCodec(encodeType);
        if (rpcCodec == null) {
            log.warn("不支持的RPC编码类型: {}", encodeType);
            rpcRes.error("不支持的RPC编码类型: " + encodeType);
            return false;
        }

        // 解析请求体
        RpcRequestBody requestBody = rpcCodec.decode(reqPacket.getBody());

        // 校验请求参数
        if (requestBody == null ||
            StringUtils.isEmpty(requestBody.getClassName()) ||
            StringUtils.isEmpty(requestBody.getMethodName())
        ) {
            log.warn("收到无效的RPC请求: {}", JsonUtils.toJson(requestBody));

            rpcRes.error("无效的请求参数");
            return false;
        }

        // 将请求体存入上下文
        context.put(MagicValue.BODY, requestBody);

        return true;
    }

}
