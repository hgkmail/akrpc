package com.shift.akrpc.common.enums;

/**
 * 错误码枚举类
 * <p>错误码格式：系统模块 + 错误类型 + 具体错误编号
 * <ul>示例：
 * <li> 4(客户端错误4xx) + 01(参数错误) + 01(具体错误) </li>
 * <li> 5(服务端错误5xx) + 01(系统内部错误) + 01(具体错误) </li>
 * </ul>
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/5
 */
public enum ErrorCode {

    // ========================== 成功 ==========================
    /** 操作成功 */
    SUCCESS(0, "操作成功"),

    // ========================== 未知错误 ==========================

    /** 未知错误 */
    UNKNOWN_ERROR(-1, "未知错误"),

    // ========================== 客户端错误 ==========================
    // 4 01 参数错误
    /** 参数校验失败 */
    PARAM_INVALID(40101, "参数校验失败"),

    /** 请求参数为空 */
    PARAM_IS_NULL(40102, "请求参数为空"),

    /** 参数格式错误 */
    PARAM_FORMAT_ERROR(40103, "参数格式错误"),

    /** 参数类型不匹配 */
    PARAM_TYPE_MISMATCH(40104, "参数类型不匹配"),

    /** 参数超出范围 */
    PARAM_OUT_OF_RANGE(40105, "参数超出允许范围"),

    /** 必填参数缺失 */
    PARAM_REQUIRED_MISSING(40106, "必填参数缺失"),

    // 4 02 用户认证错误
    /** 用户未登录 */
    USER_NOT_LOGIN(40201, "用户未登录"),

    /** 登录已过期 */
    LOGIN_EXPIRED(40202, "登录已过期，请重新登录"),

    /** 账号或密码错误 */
    ACCOUNT_PASSWORD_ERROR(40203, "账号或密码错误"),

    /** 验证码错误 */
    CAPTCHA_ERROR(40204, "验证码错误"),

    /** 账号被禁用 */
    ACCOUNT_DISABLED(40205, "账号已被禁用"),

    // 4 03 权限错误
    /** 无访问权限 */
    ACCESS_DENIED(40301, "无访问权限"),

    /** 操作权限不足 */
    PERMISSION_DENIED(40302, "操作权限不足"),

    /** 访问频率过高 */
    ACCESS_FREQUENCY_LIMIT(40303, "访问频率过高，请稍后再试"),

    // 4 04 资源不存在
    /** 数据不存在 */
    DATA_NOT_FOUND(40401, "请求的数据不存在"),

    /** 用户不存在 */
    USER_NOT_FOUND(40402, "用户不存在"),

    /** 资源不存在 */
    RESOURCE_NOT_FOUND(40403, "请求的资源不存在"),

    /** 接口不存在 */
    API_NOT_FOUND(40404, "请求的接口不存在"),

    // 4 05 请求方法错误
    /** 请求方法不支持 */
    METHOD_NOT_ALLOWED(40501, "请求方法不支持"),

    /** 不支持的媒体类型 */
    UNSUPPORTED_MEDIA_TYPE(40502, "不支持的媒体类型"),

    // ========================== 服务端错误 ==========================
    // 5 01 系统内部错误
    /** 系统内部错误 */
    SYSTEM_ERROR(50101, "系统内部错误"),

    /** 服务器异常 */
    SERVER_ERROR(50102, "服务器异常，请稍后再试"),

    /** 数据库异常 */
    DATABASE_ERROR(50103, "数据库操作异常"),

    /** 缓存服务异常 */
    CACHE_ERROR(50104, "缓存服务异常"),

    /** 文件系统异常 */
    FILE_SYSTEM_ERROR(50105, "文件系统异常"),

    /** 网络通信异常 */
    NETWORK_ERROR(50106, "网络通信异常"),

    // 5 02 第三方服务错误
    /** 第三方服务异常 */
    THIRD_PARTY_ERROR(50201, "第三方服务异常"),

    /** 外部接口调用失败 */
    EXTERNAL_API_ERROR(50202, "外部接口调用失败"),

    /** 消息队列异常 */
    MQ_ERROR(50203, "消息队列服务异常"),

    // 5 03 业务逻辑错误
    /** 业务逻辑异常 */
    BUSINESS_ERROR(50301, "业务逻辑异常"),

    /** 数据状态异常 */
    DATA_STATE_ERROR(50302, "数据状态异常"),

    /** 重复操作 */
    DUPLICATE_OPERATION(50303, "请勿重复操作"),

    /** 操作冲突 */
    OPERATION_CONFLICT(50304, "操作冲突，请检查数据状态");

    private final int code;
    private final String desc;

    ErrorCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    // ========================== 工具方法 ==========================

    /**
     * 根据错误码获取枚举实例
     * @param code 错误码
     * @return 错误码枚举
     */
    public static ErrorCode getByCode(int code) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.code == code) {
                return errorCode;
            }
        }
        return null;
    }

    /**
     * 根据错误码获取错误描述
     * @param code 错误码
     * @return 错误描述
     */
    public static String getDescByCode(int code) {
        ErrorCode errorCode = getByCode(code);
        return errorCode != null ? errorCode.getDesc() : "未知错误";
    }

    /**
     * 判断是否为客户端错误 (4开头)
     * @param code 错误码
     * @return true: 客户端错误, false: 服务端错误或其他
     */
    public static boolean isClientError(int code) {
        return code >= 40000 && code < 50000;
    }

    /**
     * 判断是否为服务端错误 (5开头)
     * @param code 错误码
     * @return true: 服务端错误, false: 客户端错误或其他
     */
    public static boolean isServerError(int code) {
        return code >= 50000 && code < 60000;
    }

    /**
     * 判断是否为参数错误 (401开头)
     * @param code 错误码
     * @return true: 参数错误, false: 其他错误
     */
    public static boolean isParamError(int code) {
        return code >= 40100 && code < 40200;
    }

    /**
     * 判断操作是否成功
     * @param code 错误码
     * @return true: 成功, false: 失败
     */
    public static boolean isSuccess(int code) {
        return code == 0;
    }

    @Override
    public String toString() {
        return "ErrorCode{" +
                "code=" + code +
                ", desc='" + desc + '\'' +
                '}';
    }
}
