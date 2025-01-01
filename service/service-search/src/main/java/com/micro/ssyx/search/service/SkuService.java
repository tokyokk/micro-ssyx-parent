package com.micro.ssyx.search.service;

import com.micro.ssyx.model.search.SkuEs;
import com.micro.ssyx.vo.search.SkuEsQueryVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author micro
 * @description
 * @date 2024/5/6 20:48
 * @github https://github.com/microsbug
 */
public interface SkuService {
    /**
     * 上架商品
     *
     * @param skuId 商品id
     * @return 商品上架是否成功
     */
    Boolean upperSku(Long skuId);

    /**
     * 下架商品
     *
     * @param skuId 商品id
     * @return 商品下架是否成功
     */
    Boolean lowerSku(Long skuId);

    /**
     * 获取爆品商品
     *
     * @return 爆品商品
     */
    List<SkuEs> findHotSkuList();

    /**
     * 搜索商品
     *
     * @param pageable     分页信息
     * @param skuEsQueryVo 查询条件
     * @return 商品列表
     */
    Page<SkuEs> search(Pageable pageable, SkuEsQueryVo skuEsQueryVo);

    /**
     * 更新商品热度
     *
     * @param skuId skuId
     * @return 是否更新成功
     */
    Boolean incrHotScore(Long skuId);

}
