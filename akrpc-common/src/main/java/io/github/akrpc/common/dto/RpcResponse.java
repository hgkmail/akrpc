package io.github.akrpc.common.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;

/**
 * RPC 响应对象
 *
 * @author Kim Huang
 * @version 1.0
 * @see RpcRequestBody
 * @since 2026/1/5
 */
@Getter
@Setter
public class RpcResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String requestId;

    private Object result;
    private String error;
    private boolean success;

    public RpcResponse(String requestId) {
        this.requestId = requestId;
    }

    /**
     * 创建成功的响应对象
     */
    public void success(Object result) {
        this.setSuccess(true);
        this.setResult(result);
        this.setError(StringUtils.EMPTY);
    }

    /**
     * 创建失败的响应对象
     */
    public void error(String error) {
        this.setSuccess(false);
        this.setError(error);
        this.setResult(null);
    }

}
