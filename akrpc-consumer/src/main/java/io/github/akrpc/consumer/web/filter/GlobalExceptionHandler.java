package io.github.akrpc.consumer.web.filter;

import io.github.akrpc.common.exception.BaseExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/6
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends BaseExceptionHandler {

//    @InitBinder
//    public void initBinder(WebDataBinder binder, WebRequest request) {
//        // 转换日期
//        DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        // CustomDateEditor 为自定义日期编辑器
//        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
//    }

}
