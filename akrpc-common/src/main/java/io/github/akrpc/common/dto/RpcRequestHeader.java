package io.github.akrpc.common.dto;

import io.github.akrpc.common.constant.MagicValue;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;

import java.io.Serial;
import java.io.Serializable;

/**
 * RPC 请求头
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/11
 */
@Getter
@Setter
public class RpcRequestHeader implements Serializable {

    public static final String HEADER_MAGIC = "ak-rpc-magic";
    public static final String HEADER_VERSION = "ak-rpc-ver";
    public static final String HEADER_ENCODE = "ak-rpc-encode";
    public static final String HEADER_GZIP = "ak-rpc-gzip";
    public static final String HEADER_REQUEST_ID = "ak-rpc-request-id";

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 校验头
     */
    private String magic = MagicValue.MAGIC_WORD;

    /**
     * 结构体版本号
     */
    private byte ver = 1;

    /**
     * 编码类型 1-JSON 2-JDK自带序列化 3-msgpack 4-kryo 5-hessian
     */
    private byte encode = 1;

    /**
     * 是否启用gzip压缩 0-不压缩 1-压缩
     */
    private byte gzip = 0;

    /**
     * 请求唯一标识
     */
    private String requestId;

    /**
     * 将请求头转换为 HttpHeaders
     */
    public HttpHeaders toHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_MAGIC, magic);
        headers.add(HEADER_VERSION, Byte.toString(ver));
        headers.add(HEADER_ENCODE, Byte.toString(encode));
        headers.add(HEADER_GZIP, Byte.toString(gzip));
        headers.add(HEADER_REQUEST_ID, requestId);
        return headers;
    }

    /**
     * 从 HttpHeaders 创建 RpcRequestHeader
     */
    public static RpcRequestHeader fromHttpHeaders(HttpHeaders headers) {
        RpcRequestHeader header = new RpcRequestHeader();
        header.setMagic(headers.getFirst(HEADER_MAGIC));
        header.setVer(getFromHeaders(headers, HEADER_VERSION));
        header.setEncode(getFromHeaders(headers, HEADER_ENCODE));
        header.setGzip(getFromHeaders(headers, HEADER_GZIP));
        header.setRequestId(headers.getFirst(HEADER_REQUEST_ID));
        return header;
    }

    private static Byte getFromHeaders(HttpHeaders headers, String key) {
        String value = headers.getFirst(key);
        if (value != null) {
            return Byte.parseByte(value);
        }
        return 0;
    }

}
