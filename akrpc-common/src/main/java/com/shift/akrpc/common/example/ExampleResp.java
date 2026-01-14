package com.shift.akrpc.common.example;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 示例响应对象
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/14
 */
@Getter
@Setter
@SuppressWarnings("rawtypes")
public class ExampleResp implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    public static class RespItem implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private String name;
        private Long value;
    }

    private List respItemList;

    private Long total;
}
