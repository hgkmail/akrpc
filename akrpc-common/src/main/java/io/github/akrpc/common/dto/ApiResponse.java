package io.github.akrpc.common.dto;

import io.github.akrpc.common.enums.ErrorCode;
import lombok.Getter;
import lombok.Setter;

/**
 * API 响应对象
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/6
 */
@Getter
@Setter
public class ApiResponse<T> {

    /** 状态码 */
    private int code;

    /** 返回信息 */
    private String message;

    /** 返回数据 */
    private T data;

    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功响应
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getDesc(), null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getDesc(), data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(ErrorCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败响应
     */
    public static <T> ApiResponse<T> fail(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public static <T> ApiResponse<T> fail(int code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }

    public static <T> ApiResponse<T> fail(ErrorCode errorCode) {
        assert errorCode!= null: "ErrorCode 不能为 null";
        return new ApiResponse<>(errorCode.getCode(), errorCode.getDesc(), null);
    }
}
