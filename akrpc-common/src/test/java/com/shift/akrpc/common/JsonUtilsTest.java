package com.shift.akrpc.common;

import com.shift.akrpc.common.dto.RpcRequestBody;
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
        RpcRequestBody rpcRequestBody = new RpcRequestBody();
        rpcRequestBody.setVersion("123456");
        log.info("test_toJson: {}", JsonUtils.toJson(rpcRequestBody));

        assertThat(JsonUtils.toJson(rpcRequestBody).length()).
                isGreaterThan("{\"requestId\":\"123456\"}".length());
    }

    @Test
    void test_toJsonWithoutNull() {
        RpcRequestBody rpcRequestBody = new RpcRequestBody();
        rpcRequestBody.setVersion("123456");
        log.info("test_toJsonNonNull: {}", JsonUtils.toJsonWithoutNull(rpcRequestBody));

        assertThat(JsonUtils.toJsonWithoutNull(rpcRequestBody)).
                isEqualTo("{\"requestId\":\"123456\"}");
    }
}
