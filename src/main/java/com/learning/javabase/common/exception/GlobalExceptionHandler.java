package com.learning.javabase.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * 设计目的：
 * 1. 统一拦截系统异常，避免将堆栈信息直接暴露给前端。
 * 2. 规范化错误返回格式。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 兜底异常处理
     */
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e) {
        log.error("系统未知异常|System_unknown_error", e);
        return "系统繁忙，请稍后再试";
    }
}
