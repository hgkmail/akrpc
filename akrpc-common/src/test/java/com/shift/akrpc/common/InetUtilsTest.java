package com.shift.akrpc.common;

import com.shift.akrpc.common.config.InetUtilsProperties;
import com.shift.akrpc.common.utils.InetUtils;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * InetUtils 测试类
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/7
 */
@Slf4j
class InetUtilsTest {

    @Test
    void test_get_ip() {
        InetUtils inetUtils = new InetUtils(new InetUtilsProperties());
        String ipAddress = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
        log.info("本地 IP 地址: {}", ipAddress);
        Assertions.assertThat(ipAddress).isNotEqualTo("127.0.0.1").isNotEqualTo("localhost");
    }
}
