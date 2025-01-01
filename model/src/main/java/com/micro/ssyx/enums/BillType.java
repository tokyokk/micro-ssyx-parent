package com.micro.ssyx.enums;

import com.alibaba.fastjson.annotation.JSONType;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.databind.deser.std.EnumDeserializer;
import com.fasterxml.jackson.databind.ser.std.EnumSerializer;
import lombok.Getter;

@JSONType(serializer = EnumSerializer.class, deserializer = EnumDeserializer.class, serializeEnumAsJavaBean = true)
@Getter
public enum BillType {
    ORDER(0, "订单佣金"),
    WITHDRAW(1, "提现"),
    REFUND(1, "订单退款");

    @EnumValue
    private final Integer code;
    private final String comment;

    BillType(final Integer code, final String comment) {
        this.code = code;
        this.comment = comment;
    }
}