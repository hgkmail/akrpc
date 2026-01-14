package com.shift.akrpc.provider.example;

import com.shift.akrpc.common.annotation.RpcService;
import com.shift.akrpc.common.example.CalcService;
import com.shift.akrpc.common.example.ExampleReq;
import com.shift.akrpc.common.example.ExampleReq.ReqItem;
import com.shift.akrpc.common.example.ExampleResp;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
    public long add(long a, long b) {
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
    public int sumArray(Integer[] numbers) {
        return Arrays.stream(numbers).reduce(0, Integer::sum);
    }

    @Override
    public long productList(List<Long> numbers) {
        return numbers.stream().reduce(1L, (x, y) -> x * y);
    }

    @Override
    public Map<String, String> flipMap(Map<String, String> map) {
        return map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
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
