package com.micro.ssyx.search.controller;

import com.micro.ssyx.common.result.ResultResponse;
import com.micro.ssyx.model.search.SkuEs;
import com.micro.ssyx.search.service.SkuService;
import com.micro.ssyx.vo.search.SkuEsQueryVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author micro
 * @description
 * @date 2024/5/6 20:47
 * @github https://github.com/tokyokk
 */
@RestController
@RequestMapping(value = "/api/search/sku")
public class SkuApiController {

    @Resource
    private SkuService skuService;

    /**
     * 查询分类商品
     *
     * @param page         当前页码
     * @param limit        每页条数
     * @param skuEsQueryVo 查询条件
     * @return 商品分类数据
     */
    @GetMapping("{page}/{limit}")
    public ResultResponse<Page<SkuEs>> listSku(@PathVariable final Integer page,
                                               @PathVariable final Integer limit,
                                               final SkuEsQueryVo skuEsQueryVo) {
        // 0 带白哦第一页
        final Pageable pageable = PageRequest.of(page - 1, limit);
        final Page<SkuEs> pageModel = skuService.search(pageable, skuEsQueryVo);
        return ResultResponse.ok(pageModel);
    }

    /**
     * 上架商品
     *
     * @param skuId 商品id
     * @return 商品上架是否成功
     */
    @GetMapping("inner/upperSku/{skuId}")
    public ResultResponse<Boolean> upperSku(@PathVariable final Long skuId) {
        return ResultResponse.ok(skuService.upperSku(skuId));
    }

    /**
     * 下架商品
     *
     * @param skuId 商品id
     * @return 商品下架是否成功
     */
    @GetMapping("inner/lowerSku/{skuId}")
    public ResultResponse<Boolean> lowerSku(@PathVariable final Long skuId) {
        return ResultResponse.ok(skuService.lowerSku(skuId));
    }

    /**
     * @return 爆品商品数据
     */
    @GetMapping("inner/findHotSkuList")
    public List<SkuEs> findHotSkuList() {
        return skuService.findHotSkuList();
    }

    /**
     * 更新商品热度
     *
     * @param skuId skuId
     * @return 是否更新成功
     */
    @GetMapping("inner/incrHotScore/{skuId}")
    public Boolean incrHotScore(@PathVariable("skuId") final Long skuId) {
        return skuService.incrHotScore(skuId);
    }
}
