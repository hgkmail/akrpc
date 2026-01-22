package io.github.akrpc.provider.web.controller;

import io.github.akrpc.common.core.provider.interceptor.ProviderInterceptorChain;
import io.github.akrpc.common.dto.RpcRequestHeader;
import io.github.akrpc.common.dto.RpcRequestPacket;
import io.github.akrpc.common.dto.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

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

    private final ProviderInterceptorChain chain;

    public RpcProviderController(ProviderInterceptorChain chain) {
        this.chain = chain;
    }

    @PostMapping("/invoke")
    public RpcResponse invoke(@RequestBody RpcRequestPacket packet, @RequestHeader HttpHeaders headers) {
        return chain.process(packet, RpcRequestHeader.fromHttpHeaders(headers));
    }
}
