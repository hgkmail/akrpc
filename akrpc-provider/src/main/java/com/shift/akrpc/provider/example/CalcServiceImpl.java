package com.shift.akrpc.provider.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shift.akrpc.common.annotation.RpcService;
import com.shift.akrpc.common.example.CalcService;
import com.shift.akrpc.common.example.ExampleReq;
import com.shift.akrpc.common.example.ExampleReq.ReqItem;
import com.shift.akrpc.common.example.ExampleResp;
import com.shift.akrpc.common.utils.ConvertUtils;
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
    public Long productList(List numbers) {
        List<Long> nums = ConvertUtils.convert(numbers, new TypeReference<>() {});
        return nums.stream().reduce(1L,
                (x, y) -> x * y);
    }

    @Override
    public Map flipMap(Map map) {
        Map<String, String> realMap = ConvertUtils.convert(map, new TypeReference<>() {});
        return realMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    @Override
    public ExampleResp opByName(ExampleReq req) {
        ExampleResp resp = new ExampleResp();

        List<ReqItem> reqItems = ConvertUtils.convert(req.getReqItemList(), new TypeReference<>() {});

        // 按 name 相加
        if (req.getOp() == 0) {
            Map<String, Long> sums = reqItems.stream().collect(
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
            Map<String, Long> products = reqItems.stream().collect(
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
