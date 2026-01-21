package com.shift.akrpc.provider.example;

import io.github.akrpc.common.annotation.RpcService;
import io.github.akrpc.common.example.CalcService;
import io.github.akrpc.common.example.ExampleReq;
import io.github.akrpc.common.example.ExampleReq.ReqItem;
import io.github.akrpc.common.example.ExampleResp;
import io.github.akrpc.common.example.ProductListReq;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

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
    public Long add(Long a, Long b) {
        // 判空
        if (a == null) {
            a = 0L;
        }
        if (b == null) {
            b = 0L;
        }
        return a + b;
    }

    @Override
    public double subtract(double a, double b) {
        return a - b;
    }

    @Override
    public BigDecimal multiply(BigDecimal a, BigDecimal b) {
        return a.multiply(b);
    }

    @Override
    public String concat(String a, String b) {
        return a + b;
    }

    @Override
    public boolean and(boolean a, boolean b) {
        return a && b;
    }

    @Override
    public int compareDate(Date a, Date b) {
        return a.compareTo(b);
    }

    @Override
    public Long sumArray(Long[] numbers) {
        return Arrays.stream(numbers).reduce(0L, Long::sum);
    }

    @Override
    public Long productList(ProductListReq req) {
        return req.getNumbers().stream().reduce(1L, (x, y) -> x * y);
    }

    @Override
    public Map<String, String> flipMap(Map<String, String> map) {
        return map.entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    @Override
    public ExampleResp opByName(ExampleReq req) {
        ExampleResp resp = new ExampleResp();

        // 按 name 相加
        if (req.getOp() == 0) {
            Map<String, Long> sums = req.getReqItemList().stream().collect(
                    Collectors.groupingBy(ReqItem::getName, Collectors.summingLong(ReqItem::getValue)));

            resp.setRespItemList(sums.entrySet().stream().map(e -> {
                ExampleResp.RespItem item = new ExampleResp.RespItem();
                item.setName(e.getKey());
                item.setValue(e.getValue());
                return item;
            }).toList());
            resp.setTotal(sums.values().stream().reduce(0L, Long::sum));
        }

        // 按 name 相乘
        if (req.getOp() == 1) {
            Map<String, Long> products = req.getReqItemList().stream().collect(
                    Collectors.groupingBy(ReqItem::getName, Collectors.reducing(1L, ReqItem::getValue, (x, y) -> x * y)));

            resp.setRespItemList(products.entrySet().stream().map(e -> {
                ExampleResp.RespItem item = new ExampleResp.RespItem();
                item.setName(e.getKey());
                item.setValue(e.getValue());
                return item;
            }).toList());
            resp.setTotal(products.values().stream().reduce(1L, (x, y) -> x * y));
        }

        return resp;
    }
}
