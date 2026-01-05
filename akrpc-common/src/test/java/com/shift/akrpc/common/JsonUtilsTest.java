package com.shift.akrpc.common;

import com.shift.akrpc.common.dto.RpcRequest;
import com.shift.akrpc.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JSON 工具类测试
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/5
 */
@Slf4j
class JsonUtilsTest {

    @Test
    void test_toJson() {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setRequestId("123456");
        log.info("test_toJson: {}", JsonUtils.toJson(rpcRequest));

        assertThat(JsonUtils.toJson(rpcRequest).length()).
                isGreaterThan("{\"requestId\":\"123456\"}".length());
    }

    @Test
    void test_toJsonNonNull() {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setRequestId("123456");
        log.info("test_toJsonNonNull: {}", JsonUtils.toJsonNonNull(rpcRequest));

        assertThat(JsonUtils.toJsonNonNull(rpcRequest)).
                isEqualTo("{\"requestId\":\"123456\"}");
    }
}
