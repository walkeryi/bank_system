package com.bank.common;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(BusinessException.class)
    public Result<Void> business(BusinessException e) { return Result.fail(e.getMessage()); }
    @ExceptionHandler(Exception.class)
    public Result<Void> error(Exception e) { log.error("系统异常", e); return Result.fail("系统异常：" + e.getMessage()); }
}
