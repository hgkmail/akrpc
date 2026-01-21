package io.github.akrpc.common.exception;

import io.github.akrpc.common.dto.ApiResponse;
import io.github.akrpc.common.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.file.AccessDeniedException;

/**
 * 全局异常处理器
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/6
 */
@Slf4j
public class BaseExceptionHandler {

    /**
     * 权限校验异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse<Object> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',权限校验失败'{}'", requestURI, e.getMessage());
        return ApiResponse.fail(ErrorCode.ACCESS_DENIED.getCode(), "没有权限，请联系管理员授权");
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiResponse<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                                                   HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod());
        return ApiResponse.fail(ErrorCode.METHOD_NOT_ALLOWED.getCode(), e.getMessage());
    }

    /**
     * 请求路径中缺少必需的路径变量
     */
    @ExceptionHandler(MissingPathVariableException.class)
    public ApiResponse<Object> handleMissingPathVariableException(MissingPathVariableException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求路径中缺少必需的路径变量'{}',发生系统异常.", requestURI, e);
        return ApiResponse.fail(ErrorCode.PARAM_REQUIRED_MISSING.getCode(), String.format("请求路径中缺少必需的路径变量[%s]", e.getVariableName()));
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<Object> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生未知异常.", requestURI, e);
        return ApiResponse.fail(ErrorCode.SERVER_ERROR);
    }

    /**
     * 资源未找到异常
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ApiResponse<Object> handleException(NoResourceFoundException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.warn("请求地址'{}', 资源未找到.", requestURI);
        return ApiResponse.fail(ErrorCode.RESOURCE_NOT_FOUND);
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Object> handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常.", requestURI, e);
        return ApiResponse.fail(ErrorCode.SYSTEM_ERROR);
    }
}
