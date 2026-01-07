package com.shift.akrpc.consumer.web.controller;

import com.shift.akrpc.common.annotation.RpcReference;
import com.shift.akrpc.common.dto.ApiResponse;
import com.shift.akrpc.common.example.CalcService;
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

    @RpcReference(url = "http://localhost:8081")
    private CalcService calcService;

    @GetMapping("/add")
    public ApiResponse<Integer> testAdd(@RequestParam Integer a, @RequestParam Integer b) {
        int result = calcService.add(a, b);
        return ApiResponse.success(result);
    }

    @GetMapping("/sub")
    public ApiResponse<Integer> testSubtract(@RequestParam Integer a, @RequestParam Integer b) {
        int result = calcService.subtract(a, b);
        return ApiResponse.success(result);
    }

}
