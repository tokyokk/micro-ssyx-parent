package com.micro.ssyx.payment.service.impl;

import com.github.wxpay.sdk.WXPayUtil;
import com.google.common.collect.Maps;
import com.micro.ssyx.common.constant.RedisConst;
import com.micro.ssyx.enums.PaymentType;
import com.micro.ssyx.model.order.PaymentInfo;
import com.micro.ssyx.payment.service.PaymentInfoService;
import com.micro.ssyx.payment.service.WeixinService;
import com.micro.ssyx.payment.utils.ConstantPropertiesUtils;
import com.micro.ssyx.payment.utils.HttpClient;
import com.micro.ssyx.vo.user.UserLoginVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author micro
 * @description
 * @date 2024/7/8 19:15
 * @github https://github.com/tokyokk
 */
@Service
public class WeixinServiceImpl implements WeixinService {

    @Resource
    private PaymentInfoService paymentInfoService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Map<String, String> createJsapi(final String orderNo) {

        // 1 向payment_info表中添加记录，目前支付状态：正在支付中
        PaymentInfo paymentInfo = paymentInfoService.getPaymentInfoByOrderNo(orderNo, PaymentType.WEIXIN);

        if (Objects.isNull(paymentInfo)) {
            paymentInfo = paymentInfoService.savePaymentInfo(orderNo, PaymentType.WEIXIN);
        }
        // 2 封装微信支付需要的参数
        final Map<String, String> paramMap = Maps.newHashMap();
        // 1、设置参数
        paramMap.put("appid", ConstantPropertiesUtils.APPID);
        paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        paramMap.put("body", paymentInfo.getSubject());
        paramMap.put("out_trade_no", paymentInfo.getOrderNo());
        final int totalFee = paymentInfo.getTotalAmount().multiply(new BigDecimal(100)).intValue();
        paramMap.put("total_fee", String.valueOf(totalFee));
        paramMap.put("spbill_create_ip", "127.0.0.1");
        paramMap.put("notify_url", ConstantPropertiesUtils.NOTIFYURL);
        paramMap.put("trade_type", "JSAPI");

        // openid
        final UserLoginVo userLoginVo =
                (UserLoginVo) redisTemplate.opsForValue().get(RedisConst.USER_LOGIN_KEY_PREFIX + paymentInfo.getUserId());

        if (null != userLoginVo && !StringUtils.isEmpty(userLoginVo.getOpenId())) {
            paramMap.put("openid", userLoginVo.getOpenId());
        } else {
            paramMap.put("openid", "oD7av4igt-00GI8PqsIlg5FROYnI");
        }

        // 3 使用HttpClient调用微信支付接口
        final HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
        try {
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY));
            client.setHttps(true);
            client.post();

            // 4 调用微信支付接口之后，返回结果 prepay_id
            final String xml = client.getContent();
            final Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);

            // 5 封装需要数据，包含预防单标识 prepay_id
            final Map<String, String> parameterMap = Maps.newHashMap();
            final String prepayId = String.valueOf(resultMap.get("prepay_id"));
            final String packages = "prepay_id=" + prepayId;
            parameterMap.put("appId", ConstantPropertiesUtils.APPID);
            parameterMap.put("nonceStr", resultMap.get("nonce_str"));
            parameterMap.put("package", packages);
            parameterMap.put("signType", "MD5");
            parameterMap.put("timeStamp", String.valueOf(System.currentTimeMillis()));
            final String sign = WXPayUtil.generateSignature(parameterMap, ConstantPropertiesUtils.PARTNERKEY);

            // 返回结果
            final Map<String, String> result = new HashMap();
            result.put("timeStamp", parameterMap.get("timeStamp"));
            result.put("nonceStr", parameterMap.get("nonceStr"));
            result.put("signType", "MD5");
            result.put("paySign", sign);
            result.put("package", packages);

            return result;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, String> queryPayStatus(final String orderNo) {
        try {
            // 1、封装参数
            final Map<String, String> paramMap = Maps.newHashMap();
            paramMap.put("appid", ConstantPropertiesUtils.APPID);
            paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
            paramMap.put("out_trade_no", orderNo);
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());

            // 2、设置请求
            final HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY));
            client.setHttps(true);
            client.post();
            // 3、返回第三方的数据
            final String xml = client.getContent();
            // 6、转成Map
            // 7、返回
            return WXPayUtil.xmlToMap(xml);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
