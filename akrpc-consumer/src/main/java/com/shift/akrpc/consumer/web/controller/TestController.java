package com.shift.akrpc.consumer.web.controller;

import com.shift.akrpc.common.annotation.RpcReference;
import com.shift.akrpc.common.dto.ApiResponse;
import com.shift.akrpc.common.example.CalcService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Consumer 端测试接口
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/6
 */
@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @Setter
    @RpcReference(name = "akrpc-provider")
    public CalcService calcService;

    @GetMapping("/add")
    public ApiResponse<Long> testAdd(@RequestParam Long a, @RequestParam Long b) {
        Long result = calcService.add(a, b);
        return ApiResponse.success(result);
    }

    @GetMapping("/sub")
    public ApiResponse<Double> testSubtract(@RequestParam Double a, @RequestParam Double b) {
        Double result = calcService.subtract(a, b);
        return ApiResponse.success(result);
    }

}
