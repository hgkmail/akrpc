package com.shift.akrpc.common.annotation;

import java.lang.annotation.*;

/**
 * 标记服务消费者
 *
 * @author Kim Huang
 * @version 1.0
 * @see RpcService
 * @since 2026/1/4
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcReference {
    /**
     * 服务版本
     */
    String version() default "1.0";

    /**
     * 超时时间(毫秒)
     */
    long timeout() default 5000;

    /**
     * 服务提供者URL
     */
    String url() default "";

    // todo 负载均衡、重试等配置
}
