package com.micro.ssyx.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.micro.ssyx.model.product.SkuInfo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * sku信息 Mapper 接口
 * </p>
 *
 * @author micro
 * @since 2024-04-18
 */
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

    /**
     * 解锁库存
     *
     * @param skuId  商品id
     * @param skuNum 商品数量
     */
    void unlockStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    /**
     * 验证库存
     *
     * @param skuId  商品id
     * @param skuNum 商品数量
     * @return 商品信息
     */
    SkuInfo checkStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    /**
     * 锁定库存
     *
     * @param skuId  商品id
     * @param skuNum 商品数量
     * @return 锁定库存结果
     */
    Integer lockStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    void minusStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);
}
