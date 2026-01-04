package com.shift.akrpc.common;

import com.shift.akrpc.common.dto.RpcRequest;
import com.shift.akrpc.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * JSON 工具类测试
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/5
 */
@Slf4j
public class JsonUtilsTest {

    @Test
    void test_toJson() {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setRequestId("123456");
        log.info("test_toJson: {}", JsonUtils.toJson(rpcRequest));

        Assertions.assertThat(JsonUtils.toJson(rpcRequest).length()).
                isGreaterThan("{\"requestId\":\"123456\"}".length());
    }

    @Test
    void test_toJsonNonNull() {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setRequestId("123456");
        log.info("test_toJsonNonNull: {}", JsonUtils.toJsonNonNull(rpcRequest));

        Assertions.assertThat(JsonUtils.toJsonNonNull(rpcRequest)).
                isEqualTo("{\"requestId\":\"123456\"}");
    }
}
