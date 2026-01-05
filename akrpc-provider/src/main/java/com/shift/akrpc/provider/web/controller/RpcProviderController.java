package com.shift.akrpc.provider.web.controller;

import com.shift.akrpc.common.dto.RpcRequest;
import com.shift.akrpc.common.dto.RpcResponse;
import com.shift.akrpc.provider.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.lang.reflect.Method;

/**
 * Provider 端HTTP接口
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/5
 */
@Slf4j
@RestController
@RequestMapping("/rpc")
public class RpcProviderController {

    private final ServiceRegistry serviceRegistry;

    public RpcProviderController(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @PostMapping("/invoke")
    public RpcResponse invoke(@RequestBody RpcRequest request) {
        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());

        try {
            Object service = serviceRegistry.getService(
                    request.getClassName(),
                    request.getVersion()
            );

            if (service == null) {
                response.setSuccess(false);
                response.setError("服务未找到: " + request.getClassName());
                return response;
            }

            Method method = service.getClass().getMethod(
                    request.getMethodName(),
                    request.getParameterTypes()
            );

            Object result = method.invoke(service, request.getParameters());

            response.setSuccess(true);
            response.setResult(result);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setError(e.getMessage());
            log.error("RPC调用出错: {}", e.getMessage(), e);
        }

        return response;
    }
}
