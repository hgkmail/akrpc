package io.github.akrpc.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * JSON 工具类
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/4
 */
@Slf4j
public class JsonUtils {

    private static final ObjectMapper defaultObjectMapper;

    private static final ObjectMapper nonNullObjectMapper;

    static {
        defaultObjectMapper = new ObjectMapper();

        // 忽略 null
        nonNullObjectMapper = new ObjectMapper();
        nonNullObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private JsonUtils() {}

    /**
     * 使用Jackson，将对象转换为 JSON 字符串
     */
    public static String toJson(Object obj)
    {
        try {
            return defaultObjectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("对象转换为 JSON 字符串失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 使用Jackson，将对象转换为 JSON 字符串，忽略 null 值
     */
    public static String toJsonWithoutNull(Object obj)
    {
        try {
            return nonNullObjectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("对象转换为 JSON 字符串（忽略 null 值）失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 使用Jackson，将 JSON 字符串转换为对象
     */
    public static <T> T fromJson(String json, Class<T> clazz)
    {
        try {
            return defaultObjectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.warn("JSON 字符串转换为对象失败: {}", e.getMessage());
        }
        return null;
    }

}
