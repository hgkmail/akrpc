package io.github.akrpc.common.annotation;

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
     * 服务提供者 URL
     * name 和 url 二选一
     */
    String url() default "";

    /**
     * 服务名称，用于服务发现
     * name 和 url 二选一
     */
    String name() default "";

    /**
     * 服务版本
     */
    String version() default "1.0";

    /**
     * 超时时间(毫秒)
     */
    long timeout() default 5000;

    // todo 负载均衡、重试等配置
}
