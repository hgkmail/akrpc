package com.shift.akrpc.common.example;

/**
 * 计算器服务接口
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/6
 */
public interface CalcService {

    /**
     * 加法
     */
    int add(int a, int b);

    /**
     * 减法
     */
    int subtract(int a, int b);
}
