package com.micro.ssyx.common.exception;

import com.micro.ssyx.common.result.ResultCodeEnum;
import lombok.Data;

@Data
public class SsyxException extends RuntimeException {

    /**
     * 异常状态码
     */
    private Integer code;

    /**
     * 通过状态码和错误消息创建异常对象
     *
     * @param message 消息
     * @param code    状态码
     */
    public SsyxException(final String message, final Integer code) {
        super(message);
        this.code = code;
    }

    /**
     * 接收枚举类型对象
     *
     * @param resultCodeEnum 业务枚举
     */
    public SsyxException(final ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }
}
