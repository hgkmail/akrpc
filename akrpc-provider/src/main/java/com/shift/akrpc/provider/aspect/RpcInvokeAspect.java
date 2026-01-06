package com.shift.akrpc.provider.aspect;

/**
 * RPC服务调用切面，记录调用日志和耗时
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/6
 */
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class RpcInvokeAspect {

    @Pointcut("@within(com.shift.akrpc.common.annotation.RpcService)")
    public void rpcServiceMethods() {}

    @Around("rpcServiceMethods()")
    public Object aroundRpcService(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.info("RPC服务调用开始: {}.{}", className, methodName);

        try {
            Object result = joinPoint.proceed();
            log.info("RPC服务调用成功: {}.{}, 耗时: {}ms", className, methodName, System.currentTimeMillis() - startTime);
            return result;
        } catch (Throwable throwable) {
            log.error("RPC服务调用失败: {}.{}, 耗时: {}ms, 错误: {}", className, methodName, System.currentTimeMillis() - startTime, throwable.getMessage());
            throw throwable;
        }
    }
}
