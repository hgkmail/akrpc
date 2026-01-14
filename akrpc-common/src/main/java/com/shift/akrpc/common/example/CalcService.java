package com.shift.akrpc.common.example;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 计算器服务接口
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/6
 */
public interface CalcService {

    /**
     * 整数加法
     */
    long add(long a, long b);

    /**
     * 浮点数减法
     */
    double subtract(double a, double b);

    /**
     * 金额乘法
     */
    BigDecimal multiply(BigDecimal a, BigDecimal b);

    /**
     * 字符串连接
     */
    String concat(String a, String b);

    /**
     * 布尔值与运算
     */
    boolean and(boolean a, boolean b);

    /**
     * 日期比较
     */
    int compareDate(Date a, Date b);

    /**
     * 数组求和
     */
    int sumArray(Integer[] numbers);

    /**
     * 列表求积
     */
    long productList(List<Long> numbers);

    /**
     * map 翻转
     */
    Map<String, String> flipMap(Map<String, String> map);

    /**
     * 复杂对象处理
     */
    ExampleResp opByName(ExampleReq req);
}
