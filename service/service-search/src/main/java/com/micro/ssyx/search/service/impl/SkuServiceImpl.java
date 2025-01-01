package com.micro.ssyx.search.service.impl;

import com.alibaba.nacos.common.utils.MapUtils;
import com.micro.ssyx.activity.client.ActivityFeignClient;
import com.micro.ssyx.client.product.ProductFeignClient;
import com.micro.ssyx.common.auth.AuthContextHolder;
import com.micro.ssyx.common.constant.RedisConst;
import com.micro.ssyx.enums.SkuType;
import com.micro.ssyx.model.product.Category;
import com.micro.ssyx.model.product.SkuInfo;
import com.micro.ssyx.model.search.SkuEs;
import com.micro.ssyx.search.repository.SkuRepository;
import com.micro.ssyx.search.service.SkuService;
import com.micro.ssyx.vo.search.SkuEsQueryVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author micro
 * @description
 * @date 2024/5/6 20:48
 * @github https://github.com/microsbug
 */
@Service
public class SkuServiceImpl implements SkuService {

    @Resource
    private SkuRepository skuRepository;

    @Resource
    private ProductFeignClient productFeignClient;

    @Resource
    private ActivityFeignClient activityFeignClient;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Boolean upperSku(final Long skuId) {
        // 1.通过远程调用，根据skuId获取sku信息
        final SkuInfo skuInfo = productFeignClient.getSkuInfoById(skuId);
        if (skuInfo == null) {
            return Boolean.FALSE;
        }
        final Category category = productFeignClient.getCategoryById(skuInfo.getCategoryId());

        // 2 获取数据封装到SkuEs对象
        final SkuEs skuEs = new SkuEs();
        // 封装分类
        if (category != null) {
            skuEs.setCategoryId(category.getId());
            skuEs.setCategoryName(category.getName());
        }

        // 封装sku信息部分
        skuEs.setId(skuInfo.getId());
        skuEs.setKeyword(skuInfo.getSkuName() + "," + skuEs.getCategoryName());
        skuEs.setWareId(skuInfo.getWareId());
        skuEs.setIsNewPerson(skuInfo.getIsNewPerson());
        skuEs.setImgUrl(skuInfo.getImgUrl());
        skuEs.setTitle(skuInfo.getSkuName());
        if (Objects.equals(skuInfo.getSkuType(), SkuType.COMMON.getCode())) {
            skuEs.setSkuType(0);
            skuEs.setPrice(skuInfo.getPrice().doubleValue());
            skuEs.setStock(skuInfo.getStock());
            skuEs.setSale(skuInfo.getSale());
            skuEs.setPerLimit(skuInfo.getPerLimit());
        }

        // 3.调用方法实现上架功能，添加到es
        skuRepository.save(skuEs);

        return Boolean.TRUE;
    }

    @Override
    public Boolean lowerSku(final Long skuId) {
        skuRepository.deleteById(skuId);
        return Boolean.TRUE;
    }

    @Override
    public List<SkuEs> findHotSkuList() {
        final Pageable pageable = PageRequest.of(0, 10);
        final Page<SkuEs> pageModel = skuRepository.findByOrderByHotScoreDesc(pageable);
        return pageModel.getContent();
    }

    @Override
    public Page<SkuEs> search(final Pageable pageable, final SkuEsQueryVo skuEsQueryVo) {
        // 1 向skuEsQueryVo设置wareId，当前登录用户的仓库id
        skuEsQueryVo.setWareId(AuthContextHolder.getWareId());

        final Page<SkuEs> pageModel;
        // 2 调用SkuRepository的方法实现搜索，遵循spring data 命名规范
        final String keyword = skuEsQueryVo.getKeyword();
        final Long wareId = skuEsQueryVo.getWareId();

        if (StringUtils.isEmpty(keyword)) {
            pageModel = skuRepository.findByCategoryIdAndWareId(skuEsQueryVo.getCategoryId(), wareId, pageable);
        } else {
            pageModel = skuRepository.findByKeywordAndWareId(keyword, wareId, pageable);
        }

        // 3 查询商品参加优惠活动
        final List<SkuEs> skuEsList = pageModel.getContent();
        if (!CollectionUtils.isEmpty(skuEsList)) {
            final List<Long> skuIdList = skuEsList.stream().map(SkuEs::getId).collect(Collectors.toList());
            // 根据skuId远程调用service-activity获取数据
            // 返回Map<Long, List<String>>, key是skuId值，values是规则名称列表（sku参加活动的多个规则名称列表），一个商品只能参加一个优惠活动，一个活动里面可以有多个规则
            // 例：满20减1元，满50减10元
            final Map<Long, List<String>> skuIdToRuleListMap = activityFeignClient.findActivity(skuIdList);

            // 封装数据到SkuEs对象中 ruleList 属性里面
            if (MapUtils.isNotEmpty(skuIdToRuleListMap)) {
                skuEsList.forEach(skuEs -> {
                    skuEs.setRuleList(skuIdToRuleListMap.get(skuEs.getId()));
                });
            }
        }

        return pageModel;
    }

    @Override
    public Boolean incrHotScore(final Long skuId) {
        final Double hotScore = redisTemplate.opsForZSet().incrementScore(RedisConst.HOT_SKU_KEY, "skuId:" + skuId, 1);
        if (hotScore % 10 == 0) {
            final Optional<SkuEs> optional = skuRepository.findById(skuId);
            if (optional.isPresent()) {
                final SkuEs skuEs = optional.get();
                skuEs.setHotScore(Math.round(hotScore));
                skuRepository.save(skuEs);
            }
        }
        return Boolean.TRUE;
    }
}
