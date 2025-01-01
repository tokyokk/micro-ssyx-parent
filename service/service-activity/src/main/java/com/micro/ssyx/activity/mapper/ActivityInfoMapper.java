package com.micro.ssyx.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.micro.ssyx.model.activity.ActivityInfo;
import com.micro.ssyx.model.activity.ActivityRule;
import com.micro.ssyx.model.activity.ActivitySku;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 活动表 Mapper 接口
 * </p>
 *
 * @author micro
 * @since 2024-05-13
 */
public interface ActivityInfoMapper extends BaseMapper<ActivityInfo> {

    /**
     * 查询skuId列表中存在的skuId
     *
     * @param skuIdList skuId列表
     * @return 存在的skuId列表
     */
    List<Long> selectSkuIdListExist(@Param("skuIdList") List<Long> skuIdList);

    /**
     * 根据skuId查询活动规则
     *
     * @param skuId skuId
     * @return 活动规则
     */
    List<ActivityRule> findActivityRule(@Param("skuId") Long skuId);

    /**
     * 根据skuId列表查询活动规则
     *
     * @param skuIdList skuId列表
     * @return 活动规则
     */
    List<ActivitySku> selectCartActivity(@Param("skuIdList") List<Long> skuIdList);

}
