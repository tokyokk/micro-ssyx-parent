package com.micro.ssyx.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.micro.ssyx.model.order.OrderInfo;
import com.micro.ssyx.vo.order.OrderConfirmVo;
import com.micro.ssyx.vo.order.OrderSubmitVo;
import com.micro.ssyx.vo.order.OrderUserQueryVo;

/**
 * <p>
 * 订单 服务类
 * </p>
 *
 * @author micro
 * @since 2024-07-08
 */
public interface OrderInfoService extends IService<OrderInfo> {

    /**
     * 确认订单
     *
     * @return 订单信息
     */
    OrderConfirmVo confirmOrder();

    /**
     * 生成订单
     *
     * @param orderParamVo 订单信息
     * @return 订单号
     */
    Long submitOrder(OrderSubmitVo orderParamVo);

    /**
     * 根据订单号查询订单信息
     *
     * @param orderId 订单号
     * @return 订单信息
     */
    OrderInfo getOrderInfoById(Long orderId);

    /**
     * 根据订单号查询订单信息
     *
     * @param orderNo 订单号
     * @return 订单信息
     */
    OrderInfo getOrderInfoByOrderNo(String orderNo);

    /**
     * 支付成功
     *
     * @param orderNo 订单号
     */
    void paySuccess(String orderNo);

    /**
     * 分页查询订单
     *
     * @param pageParam        分页参数
     * @param orderUserQueryVo 查询条件
     * @return 分页订单信息
     */
    IPage<OrderInfo> findUserOrderPage(Page<OrderInfo> pageParam, OrderUserQueryVo orderUserQueryVo);
}
