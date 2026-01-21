package com.shift.akrpc.consumer.web.controller;

import io.github.akrpc.common.annotation.RpcReference;
import io.github.akrpc.common.dto.ApiResponse;
import io.github.akrpc.common.example.CalcService;
import io.github.akrpc.common.example.ExampleReq;
import io.github.akrpc.common.example.ExampleResp;
import io.github.akrpc.common.example.ProductListReq;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    public ApiResponse<Long> testAdd(@RequestParam(required = false) Long a, @RequestParam(required = false) Long b) {
        Long result = calcService.add(a, b);
        return ApiResponse.success(result);
    }

    @GetMapping("/sub")
    public ApiResponse<Double> testSubtract(@RequestParam Double a, @RequestParam Double b) {
        Double result = calcService.subtract(a, b);
        return ApiResponse.success(result);
    }

    @GetMapping("/multiply")
    public ApiResponse<BigDecimal> testMultiply(@RequestParam BigDecimal a, @RequestParam BigDecimal b) {
        return ApiResponse.success(calcService.multiply(a, b));
    }

    @GetMapping("/concat")
    public ApiResponse<String> testConcat(@RequestParam String a, @RequestParam String b) {
        return ApiResponse.success(calcService.concat(a, b));
    }

    @GetMapping("and")
    public ApiResponse<Boolean> testAnd(@RequestParam Boolean a, @RequestParam Boolean b) {
        return ApiResponse.success(calcService.and(a, b));
    }

    @GetMapping("/compareDate")
    public ApiResponse<Integer> testCompareDate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date a,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date b
    ) {
        return ApiResponse.success(calcService.compareDate(a, b));
    }

    @GetMapping("/sumArray")
    public ApiResponse<Long> testSumArray(@RequestParam Long[] numbers) {
        return ApiResponse.success(calcService.sumArray(numbers));
    }

    @GetMapping("/productList")
    public ApiResponse<Long> testProductList(@RequestParam List<Long> numbers) {
        return ApiResponse.success(calcService.productList(new ProductListReq(numbers)));
    }

    @PostMapping("/flipMap")
    public ApiResponse<Map<String, String>> testFlipMap(@RequestBody Map<String, String> map) {
        return ApiResponse.success(calcService.flipMap(map));
    }

    @PostMapping("/opByName")
    public ApiResponse<ExampleResp> testOpByName(@RequestBody ExampleReq req) {
        return ApiResponse.success(calcService.opByName(req));
    }

}
