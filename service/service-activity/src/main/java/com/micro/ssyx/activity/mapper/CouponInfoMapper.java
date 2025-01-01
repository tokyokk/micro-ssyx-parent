package com.micro.ssyx.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.micro.ssyx.model.activity.CouponInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 优惠券信息 Mapper 接口
 * </p>
 *
 * @author micro
 * @since 2024-05-13
 */
public interface CouponInfoMapper extends BaseMapper<CouponInfo> {


    /**
     * 根据skuId + userId获取优惠券信息
     *
     * @param id         skuId
     * @param categoryId 分类id
     * @param userId     用户id
     * @return 优惠券信息
     */
    List<CouponInfo> selectCouponInfoList(@Param("skuId") Long id,
                                          @Param("categoryId") Long categoryId,
                                          @Param("userId") Long userId);

    /**
     * 根据userId获取用户优惠券列表
     *
     * @param userId 用户id
     * @return 优惠券列表
     */
    List<CouponInfo> selectCartCouponInfoList(@Param("userId") Long userId);
}
