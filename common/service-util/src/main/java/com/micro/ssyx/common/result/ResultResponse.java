package com.micro.ssyx.common.result;

import lombok.Data;

@Data
public class ResultResponse<T> {

    private Integer code;

    private String message;

    private T data;

    private ResultResponse() {
    }

    /**
     * 构建结果
     *
     * @param data    结果数据，即响应内容。可以是任意类型
     * @param code    结果代码
     * @param message 结果消息
     * @param <T>     数据的类型参数，泛型方法
     * @return 构建好的ResultResponse对象，包含结果代码、消息和数据
     */
    public static <T> ResultResponse<T> build(final T data, final Integer code, final String message) {

        final ResultResponse<T> result = new ResultResponse<>();
        if (data != null) {
            result.setData(data);
        }
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static <T> ResultResponse<T> build(final T data, final ResultCodeEnum resultCodeEnum) {

        final ResultResponse<T> result = new ResultResponse<>();
        if (data != null) {
            result.setData(data);
        }
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }

    /**
     * 构建成功的结果
     *
     * @param data 结果数据，即响应内容。可以是任意类型
     * @param <T>  数据的类型参数，泛型方法
     * @return 构建好的ResultResponse对象，包含结果代码、消息和数据
     */
    public static <T> ResultResponse<T> ok(final T data) {
        return build(data, ResultCodeEnum.SUCCESS);
    }

    /**
     * 构建失败的结果
     *
     * @param data 结果数据，即响应内容。可以是任意类型
     * @param <T>  数据的类型参数，泛型方法
     * @return 构建好的ResultResponse对象，包含结果代码、消息和数据
     */
    public static <T> ResultResponse<T> fail(final T data) {
        return build(data, ResultCodeEnum.FAIL);
    }
}
