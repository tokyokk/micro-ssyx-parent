package com.micro.ssyx.common.exception;

import com.micro.ssyx.common.result.ResultResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 全局异常处理
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResultResponse<Object> error(final Exception e) {
        e.printStackTrace();
        return ResultResponse.fail(null);
    }

    /**
     * 自定义异常处理
     */
    @ResponseBody
    @ExceptionHandler(SsyxException.class)
    public ResultResponse<Object> error(final SsyxException e) {
        e.printStackTrace();
        return ResultResponse.build(null, e.getCode(), e.getMessage());
    }
}
