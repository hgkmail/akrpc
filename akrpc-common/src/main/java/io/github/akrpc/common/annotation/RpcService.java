package io.github.akrpc.common.annotation;

import java.lang.annotation.*;

/**
 * 标记服务提供者
 *
 * @author Kim Huang
 * @version 1.0
 * @see RpcReference
 * @since 2026/1/4
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcService {

    /**
     * 服务接口类
     */
    Class<?> value();

    /**
     * 服务版本
     */
    String version() default "1.0";
}
