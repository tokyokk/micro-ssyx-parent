package com.micro.ssyx.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum SkuType {
    /**
     * 普通商品
     */
    COMMON(0, "普通"),
    /**
     * 秒杀商品
     */
    SECKILL(1, "秒杀");

    @EnumValue
    private final Integer code;
    private final String comment;

    SkuType(final Integer code, final String comment) {
        this.code = code;
        this.comment = comment;
    }
}