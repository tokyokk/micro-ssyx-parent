package com.micro.ssyx.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.micro.ssyx.model.product.SkuInfo;
import com.micro.ssyx.vo.product.SkuInfoQueryVo;
import com.micro.ssyx.vo.product.SkuInfoVo;
import com.micro.ssyx.vo.product.SkuStockLockVo;

import java.util.List;

/**
 * <p>
 * sku信息 服务类
 * </p>
 *
 * @author micro
 * @since 2024-04-18
 */
public interface SkuInfoService extends IService<SkuInfo> {

    /**
     * sku列表数据
     *
     * @param skuInfoPage    分页参数
     * @param skuInfoQueryVo 查询条件
     * @return 列表数据
     */
    IPage<SkuInfo> selectPageSkuInfo(Page<SkuInfo> skuInfoPage, SkuInfoQueryVo skuInfoQueryVo);

    /**
     * 保存sku信息
     *
     * @param skuInfoVo sku信息
     * @return 是否保存成功
     */
    Boolean saveSkuInfo(SkuInfoVo skuInfoVo);

    /**
     * 根据id获取sku信息
     *
     * @param id sku信息id
     * @return sku信息
     */
    SkuInfo getSkuInfo(Long id);

    /**
     * 修改sku信息
     *
     * @param skuInfoVo sku信息
     * @return 是否修改成功
     */
    Boolean updateSkuInfo(SkuInfoVo skuInfoVo);

    /**
     * 修改sku上架状态
     *
     * @param skuId  sku信息id
     * @param status 上架状态
     * @return 是否修改成功
     */
    Boolean check(Long skuId, Integer status);

    /**
     * 商品上下架
     *
     * @param skuId  sku信息id
     * @param status 上架状态/下架状态
     * @return 是否修改成功
     */
    Boolean publish(Long skuId, Integer status);

    /**
     * 新人专享
     *
     * @param skuId  sku信息id
     * @param status 新人专享状态
     * @return 是否修改成功
     */
    Boolean isNewPerson(Long skuId, Integer status);

    /**
     * 根据skuId列表查询商品信息
     *
     * @param skuIdList skuId列表
     * @return 商品信息列表
     */
    List<SkuInfo> findSkuInfoList(List<Long> skuIdList);

    /**
     * 根据关键字查询sku信息
     *
     * @param keyword 关键字
     * @return sku信息
     */
    List<SkuInfo> findSkuInfoByKeyword(String keyword);

    /**
     * 查询新人专享商品
     *
     * @return sku信息
     */
    List<SkuInfo> findNewPersonList();

    /**
     * 根据skuId查询sku信息
     *
     * @param skuId skuId
     * @return sku信息
     */
    SkuInfoVo getSkuInfoVo(Long skuId);

    /**
     * 锁定库存
     *
     * @param skuStockLockVoList sku库存锁定信息
     * @param orderNo            订单号
     * @return 是否锁定成功
     */
    Boolean checkAndLock(List<SkuStockLockVo> skuStockLockVoList, String orderNo);

    /**
     * 扣减库存成功，更新订单状态
     *
     * @param orderNo 订单号
     */
    void minusStock(String orderNo);
}
