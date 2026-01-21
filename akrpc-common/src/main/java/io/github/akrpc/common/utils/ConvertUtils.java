package io.github.akrpc.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 类型转换工具类
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/14
 */
public class ConvertUtils {

    /**
     * 线程安全的全局 ObjectMapper 实例
     */
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
    }

    private ConvertUtils() {}

    /**
     * 类型转换，泛型类型会被擦除，List&lt;Long&gt; 与 List&lt;Integer&gt; 无法区分
     * @see #convert(Object, TypeReference)
     * @see #convert(Object, JavaType)
     */
    public static <T> T convert(Object fromValue, Class<T> toValueType) {
        return objectMapper.convertValue(fromValue, toValueType);
    }

    /**
     * 类型转换，支持泛型类型转换
     */
    public static <T> T convert(Object fromValue, TypeReference<T> toValueType) {
        // TypeReference 故意设计成一个抽象类，用子类来捕获泛型类型信息
        return objectMapper.convertValue(fromValue, toValueType);
    }

    /**
     * 类型转换，支持泛型类型转换
     */
    public static <T> T convert(Object fromValue, JavaType toValueType) {
        // objectMapper.getTypeFactory().constructParametricType() 可用于构造支持泛型的 JavaType
        return objectMapper.convertValue(fromValue, toValueType);
    }

    /**
     * 获取 ObjectMapper 实例
     */
    public static ObjectMapper getObjMapper() {
        return objectMapper;
    }

    public static Byte bool2Byte(Boolean value) {
        return (byte) (Boolean.TRUE.equals(value) ? 1 : 0);
    }

    public static Boolean byte2Bool(Byte value) {
        return value != null && value != 0;
    }

}
