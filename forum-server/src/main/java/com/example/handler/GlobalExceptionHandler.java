package com.example.handler;

import com.example.constant.Result;
import com.example.constant.ResultCode;
import com.example.exception.RateLimitException;
import com.example.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.List;
import java.util.Objects;

/**
 * 全局异常处理
 * @author zhw
 */
@Slf4j
@RestControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        log.error("系统内部异常，异常信息：", e);
        return Result.error(ResultCode.INTERFACE_INNER_INVOKE_ERROR);
    }

    @ExceptionHandler(value = SystemException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleParamsInvalidException(SystemException e) {
        log.error("系统错误：{}", e.getMessage());
        return new Result<>(500, e.getMessage());
    }

    /**
     * 统一处理请求参数校验(实体对象传参)
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> validExceptionHandler(BindException e) {
        StringBuilder message = new StringBuilder();
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        for (FieldError error : fieldErrors) {
            message.append(error.getField()).append(error.getDefaultMessage()).append(",");
        }
        message = new StringBuilder(message.substring(0, message.length() - 1));
        return new Result<>(400,message.toString());

    }

    /**
     * 接口被限流
     */
    @ExceptionHandler(RateLimitException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> rateLimitException(RateLimitException e){
        return new Result<>(500,e.getMessage());
    }

    /**
     * valid注解验证失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.error(e.getMessage(), e);
        String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        return Result.error(ResultCode.PARAM_TYPE_BIND_ERROR,message);
    }

}
