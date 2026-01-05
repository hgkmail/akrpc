package com.shift.akrpc.provider.example;

import com.shift.akrpc.common.annotation.RpcService;
import com.shift.akrpc.common.example.CalcService;
import org.springframework.stereotype.Service;

/**
 * 计算器服务实现
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/6
 */
@Service
@RpcService(value = CalcService.class)
public class CalcServiceImpl implements CalcService {
    @Override
    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public int subtract(int a, int b) {
        return a - b;
    }
}
