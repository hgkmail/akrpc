package com.shift.akrpc.common.example;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 示例请求对象
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/14
 */
@Getter
@Setter
public class ExampleReq implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    public static class ReqItem implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private String name;
        private Long value;
    }

    private List<ReqItem> reqItemList;

    /**
     * 操作类型 0: sum, 1: multiply
     */
    private Integer op;
}
