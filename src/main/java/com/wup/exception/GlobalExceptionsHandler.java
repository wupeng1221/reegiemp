package com.wup.exception;

import com.wup.common.Result;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionsHandler {
    @ExceptionHandler
    public Result<String> handler(DuplicateKeyException exception) {
        // 判断是否是用户名重复
        if (exception.getMessage().contains("Duplicate entry")) {
            String[] strings = exception.getMessage().split(" ");
            String msg = "用户名" + strings[9] + "已存在";
            return Result.error(msg);
        }
        return Result.error("未知错误");
    }
}
