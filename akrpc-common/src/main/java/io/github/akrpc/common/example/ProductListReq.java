package io.github.akrpc.common.example;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 示例请求对象 - 列表乘积
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/15
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductListReq implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private List<Long> numbers;

}
