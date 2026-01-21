package io.github.akrpc.common;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.akrpc.common.utils.ConvertUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

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

        List<Long> longList = Arrays.asList(1L, 2L, 3L);
        // Using TypeReference for conversion
        List<Integer> list1 = ConvertUtils.convert(longList, new TypeReference<>() {});
        // Using JavaType for conversion
        List<Integer> list2 = ConvertUtils.convert(longList,
                ConvertUtils.getObjMapper().getTypeFactory().constructParametricType(List.class, Integer.class));
        Assertions.assertTrue(CollectionUtils.isEqualCollection(list1, list2));
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
