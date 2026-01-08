package com.shift.akrpc.common.annotation;

import com.shift.akrpc.common.core.provider.ProviderConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用 RPC 服务提供者注解
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/9
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(ProviderConfig.class)
public @interface EnableRpcProvider {
}
