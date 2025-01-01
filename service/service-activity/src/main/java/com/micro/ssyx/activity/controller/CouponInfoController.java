package com.micro.ssyx.activity.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.micro.ssyx.activity.service.CouponInfoService;
import com.micro.ssyx.common.result.ResultResponse;
import com.micro.ssyx.model.activity.CouponInfo;
import com.micro.ssyx.vo.activity.CouponRuleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 优惠券信息 前端控制器
 * </p>
 *
 * @author micro
 * @since 2024-05-13
 */
@RestController
@RequestMapping("/admin/activity/couponInfo")
public class CouponInfoController {

    @Autowired
    private CouponInfoService couponInfoService;

    /**
     * 分页查询优惠券
     *
     * @param page  当前页
     * @param limit 每页显示条数
     * @return 优惠券列表
     */
    @GetMapping("{page}/{limit}")
    public ResultResponse<IPage<CouponInfo>> selectPageCouponInfo(@PathVariable final Long page, @PathVariable final Long limit) {
        final Page<CouponInfo> pageParam = new Page<>(page, limit);
        final IPage<CouponInfo> pageModel = couponInfoService.selectPageCouponInfo(pageParam);
        return ResultResponse.ok(pageModel);
    }

    /**
     * 获取优惠券
     *
     * @param id 优惠券id
     * @return 优惠券信息
     */
    @GetMapping("get/{id}")
    public ResultResponse<CouponInfo> get(@PathVariable final String id) {
        final CouponInfo couponInfo = couponInfoService.getCouponInfo(id);
        return ResultResponse.ok(couponInfo);
    }

    /**
     * 新增优惠券
     *
     * @param couponInfo 优惠券信息
     * @return 新增结果
     */
    @PostMapping("save")
    public ResultResponse<Boolean> save(@RequestBody final CouponInfo couponInfo) {
        return ResultResponse.ok(couponInfoService.save(couponInfo));
    }

    /**
     * 修改优惠券
     *
     * @param couponInfo 优惠券信息
     * @return 修改结果
     */
    @PutMapping("update")
    public ResultResponse<Boolean> updateById(@RequestBody final CouponInfo couponInfo) {
        return ResultResponse.ok(couponInfoService.updateById(couponInfo));
    }

    /**
     * 删除优惠券
     *
     * @param id 优惠券id
     * @return 删除结果
     */
    @DeleteMapping("remove/{id}")
    public ResultResponse<Boolean> remove(@PathVariable final String id) {
        return ResultResponse.ok(couponInfoService.removeById(id));
    }

    /**
     * 根据id列表删除优惠券
     *
     * @param idList 优惠券id列表
     * @return 删除结果
     */
    @DeleteMapping("batchRemove")
    public ResultResponse<Boolean> batchRemove(@RequestBody final List<String> idList) {
        return ResultResponse.ok(couponInfoService.removeByIds(idList));
    }

    /**
     * 根据优惠卷id查询规则数据
     *
     * @param id 优惠券id
     * @return 优惠券规则数据
     */
    @GetMapping("findCouponRuleList/{id}")
    public ResultResponse<Map<String, Object>> findCouponRuleList(@PathVariable("id") final Long id) {
        return ResultResponse.ok(couponInfoService.findCouponRuleList(id));
    }

    /**
     * 新增活动
     *
     * @param couponRuleVo 活动信息
     * @return 新增结果
     */
    @PostMapping("saveCouponRule")
    public ResultResponse<Boolean> saveCouponRule(@RequestBody final CouponRuleVo couponRuleVo) {
        return ResultResponse.ok(couponInfoService.saveCouponRule(couponRuleVo));
    }

}

