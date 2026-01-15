package com.shift.akrpc.common.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * RPC 服务提供者信息
 * <p>唯一标识：服务名称 + 服务地址 + 服务端口
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/7
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RpcProvider implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 服务名称
     */
    private String name;

    /**
     * 服务地址
     */
    private String address;

    /**
     * 服务端口
     */
    private Integer port;

    /**
     * 创建时间，时间戳，单位秒
     */
    private Long createTime;

    /**
     * 更新时间，时间戳，单位秒
     */
    private Long updateTime;

    // todo 版本号、权重、分组、backup 等信息

}
