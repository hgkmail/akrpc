package com.shift.akrpc.common;

import com.shift.akrpc.common.utils.ConvertUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * ConvertUtils 测试类
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/14
 */
class ConvertUtilsTest {

    @Test
    void test_convert() {
        String str = "12345";
        Long intValue = ConvertUtils.convert(str, Long.class);
        Assertions.assertEquals(12345L, intValue);

        intValue = ConvertUtils.convert(null, Long.class);
        Assertions.assertNull(intValue);
    }

    @Test
    void test_bool2byte() {
        Byte byteTrue = ConvertUtils.bool2Byte(true);
        Byte byteFalse = ConvertUtils.bool2Byte(false);
        Assertions.assertEquals((byte) 1, byteTrue);
        Assertions.assertEquals((byte) 0, byteFalse);
    }

    @Test
    void test_byte2bool() {
        Boolean boolTrue = ConvertUtils.byte2Bool((byte) 1);
        Boolean boolFalse = ConvertUtils.byte2Bool((byte) 0);
        Boolean boolNull = ConvertUtils.byte2Bool(null);
        Assertions.assertTrue(boolTrue);
        Assertions.assertFalse(boolFalse);
        Assertions.assertFalse(boolNull);
    }
}
