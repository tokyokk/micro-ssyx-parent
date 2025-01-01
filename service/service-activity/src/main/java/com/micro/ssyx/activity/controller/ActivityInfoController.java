package com.micro.ssyx.activity.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.micro.ssyx.activity.service.ActivityInfoService;
import com.micro.ssyx.common.result.ResultResponse;
import com.micro.ssyx.model.activity.ActivityInfo;
import com.micro.ssyx.model.product.SkuInfo;
import com.micro.ssyx.vo.activity.ActivityRuleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 活动表 前端控制器
 * </p>
 *
 * @author micro
 * @since 2024-05-13
 */
@RestController
@RequestMapping("/admin/activity/activityInfo")
public class ActivityInfoController {

    @Autowired
    private ActivityInfoService activityInfoService;

    /**
     * 分页查询营销活动列表
     *
     * @param page  当前页码
     * @param limit 每页记录数
     * @return 分页列表
     */
    @GetMapping("/{page}/{limit}")
    public ResultResponse<IPage<ActivityInfo>> list(@PathVariable final Long page, @PathVariable final Long limit) {
        final Page<ActivityInfo> pageParam = new Page<>(page, limit);
        return ResultResponse.ok(activityInfoService.selectPage(pageParam));
    }

    /**
     * 根据id查询营销活动
     *
     * @param id 营销活动id
     * @return 营销活动
     */
    @GetMapping("/get/{id}")
    public ResultResponse<ActivityInfo> get(@PathVariable final Long id) {
        final ActivityInfo activityInfo = activityInfoService.getById(id);
        activityInfo.setActivityTypeString(activityInfo.getActivityType().getComment());
        return ResultResponse.ok(activityInfo);
    }

    /**
     * 修改营销活动
     *
     * @param activityInfo 营销活动
     * @return 是否修改成功
     */
    @GetMapping("update")
    public ResultResponse<Boolean> updateById(@RequestBody final ActivityInfo activityInfo) {
        return ResultResponse.ok(activityInfoService.updateById(activityInfo));
    }

    /**
     * 删除营销活动
     *
     * @param id 营销活动id
     * @return 是否删除成功
     */
    @DeleteMapping("remove/{id}")
    public ResultResponse<Boolean> removeById(@PathVariable final Long id) {
        return ResultResponse.ok(activityInfoService.removeById(id));
    }

    /**
     * 添加营销活动
     *
     * @param activityInfo 营销活动
     * @return 是否添加成功
     */
    @PostMapping("save")
    public ResultResponse<Boolean> save(@RequestBody final ActivityInfo activityInfo) {
        return ResultResponse.ok(activityInfoService.save(activityInfo));
    }

    /**
     * 批量删除营销活动
     *
     * @param idList 营销活动id列表
     * @return 是否删除成功
     */
    @DeleteMapping("batchRemove")
    public ResultResponse<Boolean> batchRemove(@RequestBody final List<Long> idList) {
        return ResultResponse.ok(activityInfoService.removeByIds(idList));
    }

    /**
     * 根据活动id查询活动规则列表
     *
     * @param id 活动id
     * @return 活动规则列表
     */
    @GetMapping("findActivityRuleList/{id}")
    public ResultResponse<Map<String, Object>> findActivityRuleList(@PathVariable final Long id) {
        final Map<String, Object> activityRuleMap = activityInfoService.findActivityRuleList(id);
        return ResultResponse.ok(activityRuleMap);
    }

    /**
     * 保存活动规则数据
     *
     * @param activityRuleVo 活动规则
     * @return 是否保存成功
     */
    @PostMapping("saveActivityRule")
    public ResultResponse<Boolean> saveActivityRule(@RequestBody final ActivityRuleVo activityRuleVo) {
        return ResultResponse.ok(activityInfoService.saveActivityRule(activityRuleVo));
    }

    /**
     * 根据关键字查询商品信息
     *
     * @param keyword 关键字
     * @return 商品信息列表
     */
    @GetMapping("findSkuInfoByKeyword/{keyword}")
    public ResultResponse<List<SkuInfo>> findSkuInfoByKeyword(@PathVariable final String keyword) {
        return ResultResponse.ok(activityInfoService.findSkuInfoByKeyword(keyword));
    }
}

