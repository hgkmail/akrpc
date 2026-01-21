package io.github.akrpc.common.annotation;

import io.github.akrpc.common.core.discovery.DiscoveryConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用 RPC 服务发现注解
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/9
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(DiscoveryConfig.class)
public @interface EnableRpcDiscovery {
}
