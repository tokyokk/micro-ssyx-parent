package com.micro.ssyx.product.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.micro.ssyx.common.result.ResultResponse;
import com.micro.ssyx.model.product.SkuInfo;
import com.micro.ssyx.product.service.SkuInfoService;
import com.micro.ssyx.vo.product.SkuInfoQueryVo;
import com.micro.ssyx.vo.product.SkuInfoVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * sku信息 前端控制器
 * </p>
 *
 * @author micro
 * @since 2024-04-18
 */
@RestController
@RequestMapping("/admin/product/skuInfo")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SkuInfoController {

    private final SkuInfoService skuInfoService;

    /**
     * sku列表数据
     *
     * @param page           当前页码
     * @param limit          每夜条数
     * @param skuInfoQueryVo 查询条件
     * @return 列表数据
     */
    @GetMapping("{page}/{limit}")
    public ResultResponse<Object> index(@PathVariable final Long page,
                                        @PathVariable final Long limit,
                                        final SkuInfoQueryVo skuInfoQueryVo) {
        final Page<SkuInfo> skuInfoPage = new Page<>(page, limit);
        final IPage<SkuInfo> pageModel = skuInfoService.selectPageSkuInfo(skuInfoPage, skuInfoQueryVo);
        return ResultResponse.ok(pageModel);
    }

    /**
     * 保存sku信息
     *
     * @param skuInfoVo sku信息
     * @return 保存结果
     */
    @PostMapping("save")
    public ResultResponse<Boolean> save(@RequestBody final SkuInfoVo skuInfoVo) {
        return ResultResponse.ok(skuInfoService.saveSkuInfo(skuInfoVo));
    }

    /**
     * 根据id查询sku信息
     *
     * @param id sku信息id
     * @return sku信息
     */
    @GetMapping("get/{id}")
    public ResultResponse<SkuInfo> get(@PathVariable final Long id) {
        final SkuInfo skuInfo = skuInfoService.getSkuInfo(id);
        return ResultResponse.ok(skuInfo);
    }

    /**
     * 修改sku信息
     *
     * @param skuInfoVo sku信息
     * @return 修改结果
     */
    @PutMapping("update")
    public ResultResponse<Boolean> updateById(@RequestBody final SkuInfoVo skuInfoVo) {
        return ResultResponse.ok(skuInfoService.updateSkuInfo(skuInfoVo));
    }

    /**
     * 删除sku信息
     *
     * @param id sku信息id
     * @return 删除结果
     */
    @DeleteMapping("remove/{id}")
    public ResultResponse<Boolean> removeById(@PathVariable final Long id) {
        skuInfoService.removeById(id);
        return ResultResponse.ok(true);
    }

    /**
     * 批量删除sku信息
     *
     * @param idList sku信息id列表
     * @return 删除结果
     */
    @DeleteMapping("batchRemove")
    public ResultResponse<Boolean> batchRemove(@RequestBody final List<Long> idList) {
        return ResultResponse.ok(skuInfoService.removeByIds(idList));
    }

    /**
     * 商品审核
     *
     * @param skuId  sku信息id
     * @param status 审核状态
     * @return 审核结果
     */
    @GetMapping("check/{skuId}/{status}")
    public ResultResponse<Boolean> check(@PathVariable final Long skuId,
                                         @PathVariable final Integer status) {
        return ResultResponse.ok(skuInfoService.check(skuId, status));
    }

    /**
     * 商品上下架
     *
     * @param skuId sku信息id
     * @return 上架结果/下架结果
     */
    @GetMapping("publish/{skuId}/{status}")
    public ResultResponse<Boolean> publish(@PathVariable final Long skuId,
                                           @PathVariable final Integer status) {
        return ResultResponse.ok(skuInfoService.publish(skuId, status));
    }

    /**
     * 新品推荐
     *
     * @param skuId  sku信息id
     * @param status 推荐状态
     * @return 推荐结果
     */
    @GetMapping("isNewPerson/{skuId}/{status}")
    public ResultResponse<Boolean> isNewPerson(@PathVariable final Long skuId,
                                               @PathVariable final Integer status) {
        return ResultResponse.ok(skuInfoService.isNewPerson(skuId, status));
    }
}

