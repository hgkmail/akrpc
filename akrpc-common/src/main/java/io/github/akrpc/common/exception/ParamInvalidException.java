package io.github.akrpc.common.exception;

import io.github.akrpc.common.constant.MagicValue;
import io.github.akrpc.common.enums.ErrorCode;

/**
 * 参数无效异常
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/5
 */
public class ParamInvalidException extends RuntimeException {
    public ParamInvalidException() {
        super(ErrorCode.PARAM_INVALID.getDesc());
    }

    public ParamInvalidException(String message) {
        super(ErrorCode.PARAM_INVALID.getDesc() + MagicValue.COLON + message);
    }

    public ParamInvalidException(String message, Throwable cause) {
        super(ErrorCode.PARAM_INVALID.getDesc() + MagicValue.COLON + message, cause);
    }

}
