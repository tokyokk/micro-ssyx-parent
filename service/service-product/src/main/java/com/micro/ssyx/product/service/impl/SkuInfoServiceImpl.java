package com.micro.ssyx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.ssyx.common.constant.RedisConst;
import com.micro.ssyx.common.exception.SsyxException;
import com.micro.ssyx.common.result.ResultCodeEnum;
import com.micro.ssyx.model.product.SkuAttrValue;
import com.micro.ssyx.model.product.SkuImage;
import com.micro.ssyx.model.product.SkuInfo;
import com.micro.ssyx.model.product.SkuPoster;
import com.micro.ssyx.mq.constant.MQConst;
import com.micro.ssyx.mq.service.RabbitService;
import com.micro.ssyx.product.mapper.SkuInfoMapper;
import com.micro.ssyx.product.service.SkuAttrValueService;
import com.micro.ssyx.product.service.SkuImageService;
import com.micro.ssyx.product.service.SkuInfoService;
import com.micro.ssyx.product.service.SkuPosterService;
import com.micro.ssyx.vo.product.SkuInfoQueryVo;
import com.micro.ssyx.vo.product.SkuInfoVo;
import com.micro.ssyx.vo.product.SkuStockLockVo;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * sku信息 服务实现类
 * </p>
 *
 * @author micro
 * @since 2024-04-18
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo> implements SkuInfoService {

    private final SkuImageService skuImageService;

    private final SkuAttrValueService skuAttrValueService;

    private final SkuPosterService skuPosterService;

    private final RabbitService rabbitService;

    private final RedisTemplate<String, Object> redisTemplate;

    private final RedissonClient redissonClient;

    @Override
    public IPage<SkuInfo> selectPageSkuInfo(final Page<SkuInfo> skuInfoPage, final SkuInfoQueryVo skuInfoQueryVo) {
        final LambdaQueryWrapper<SkuInfo> skuInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        skuInfoLambdaQueryWrapper.like(!StringUtils.isEmpty(skuInfoQueryVo.getSkuType()), SkuInfo::getSkuType, skuInfoQueryVo.getSkuType())
                .like(!StringUtils.isEmpty(skuInfoQueryVo.getKeyword()), SkuInfo::getSkuName, skuInfoQueryVo.getKeyword())
                .eq(!StringUtils.isEmpty(skuInfoQueryVo.getCategoryId()), SkuInfo::getCategoryId, skuInfoQueryVo.getCategoryId());

        return baseMapper.selectPage(skuInfoPage, skuInfoLambdaQueryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean saveSkuInfo(final SkuInfoVo skuInfoVo) {
        // 1.添加sku基本信息
        final SkuInfo skuInfo = new SkuInfo();
        BeanUtils.copyProperties(skuInfoVo, skuInfo);
        baseMapper.insert(skuInfo);

        // 2.添加sku海报
        saveSkuPosterList(skuInfoVo, skuInfo);

        // 3.添加sku属性
        saveSkuAttrValueList(skuInfoVo, skuInfo);

        // 4.添加sku图片
        saveSkuImageList(skuInfoVo, skuInfo);

        return Boolean.TRUE;
    }

    private void saveSkuImageList(final SkuInfoVo skuInfoVo, final SkuInfo skuInfo) {
        final List<SkuImage> skuImagesList = skuInfoVo.getSkuImagesList();
        if (!CollectionUtils.isEmpty(skuImagesList)) {
            skuImagesList.forEach(skuImage -> {
                // 向每个skuImage对象添加skuId
                skuImage.setSkuId(skuInfo.getId());
            });
            skuImageService.saveBatch(skuImagesList);
        }
    }

    private void saveSkuAttrValueList(final SkuInfoVo skuInfoVo, final SkuInfo skuInfo) {
        final List<SkuAttrValue> skuAttrValueList = skuInfoVo.getSkuAttrValueList();
        if (!CollectionUtils.isEmpty(skuAttrValueList)) {
            skuAttrValueList.forEach(skuAttrValue -> {
                // 向每个skuAttrValue对象添加skuId
                skuAttrValue.setSkuId(skuInfo.getId());
            });
            skuAttrValueService.saveBatch(skuAttrValueList);
        }
    }

    private void saveSkuPosterList(final SkuInfoVo skuInfoVo, final SkuInfo skuInfo) {
        final List<SkuPoster> skuPosterList = skuInfoVo.getSkuPosterList();
        if (!CollectionUtils.isNotEmpty(skuPosterList)) {
            skuPosterList.forEach(skuPoster -> {
                // 向每个海报对象添加skuId
                skuPoster.setSkuId(skuInfo.getId());
            });
            skuPosterService.saveBatch(skuPosterList);
        }
    }

    @Override
    public Boolean publish(final Long skuId, final Integer status) {
        // 1. 更新sku发布状态为上架
        if (status == 1) {
            final SkuInfo skuInfo = baseMapper.selectById(skuId);
            skuInfo.setPublishStatus(status);
            baseMapper.updateById(skuInfo);
            // 整合mq把数据同步到es中去
            rabbitService.sendMessage(MQConst.EXCHANGE_GOODS_DIRECT, MQConst.ROUTING_GOODS_UPPER, skuId);
            return Boolean.TRUE;
        } else {
            // 2. 更新sku发布状态为下架
            final SkuInfo skuInfo = baseMapper.selectById(skuId);
            skuInfo.setPublishStatus(status);
            baseMapper.updateById(skuInfo);
            // 整合mq把数据同步到es中去
            rabbitService.sendMessage(MQConst.EXCHANGE_GOODS_DIRECT, MQConst.ROUTING_GOODS_LOWER, skuId);
            return Boolean.TRUE;
        }
    }

    @Override
    public Boolean check(final Long skuId, final Integer status) {
        final SkuInfo skuInfo = baseMapper.selectById(skuId);
        skuInfo.setCheckStatus(status);
        return baseMapper.updateById(skuInfo) > 0;
    }

    @Override
    public Boolean updateSkuInfo(final SkuInfoVo skuInfoVo) {
        // 1. 更新sku基本信息
        final SkuInfo skuInfo = new SkuInfo();
        BeanUtils.copyProperties(skuInfoVo, skuInfo);
        baseMapper.updateById(skuInfo);

        // 2. 更新sku海报信息
        final LambdaQueryWrapper<SkuPoster> skuPosterLambdaQueryWrapper = new LambdaQueryWrapper<>();
        skuPosterLambdaQueryWrapper.eq(SkuPoster::getSkuId, skuInfoVo.getId());
        skuPosterService.remove(skuPosterLambdaQueryWrapper);
        saveSkuPosterList(skuInfoVo, skuInfo);

        // 3. 更新sku属性信息
        final LambdaQueryWrapper<SkuAttrValue> skuAttrValueLambdaQueryWrapper = new LambdaQueryWrapper<>();
        skuAttrValueLambdaQueryWrapper.eq(SkuAttrValue::getSkuId, skuInfoVo.getId());
        skuAttrValueService.remove(skuAttrValueLambdaQueryWrapper);
        saveSkuAttrValueList(skuInfoVo, skuInfo);

        // 4. 更新sku图片信息
        final LambdaQueryWrapper<SkuImage> skuImageLambdaQueryWrapper = new LambdaQueryWrapper<>();
        skuImageLambdaQueryWrapper.eq(SkuImage::getSkuId, skuInfoVo.getId());
        skuImageService.remove(skuImageLambdaQueryWrapper);
        saveSkuImageList(skuInfoVo, skuInfo);
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SkuInfo getSkuInfo(final Long id) {
        final SkuInfoVo skuInfoVo = new SkuInfoVo();

        // 1. 获取sku基本信息
        final SkuInfo skuInfo = baseMapper.selectById(id);

        // 2. 获取sku海报信息
        final List<SkuPoster> skuPosterList = skuPosterService.getSkuPosterListBySkuId(id);

        // 3. 获取sku属性信息
        final List<SkuAttrValue> skuAttrValueList = skuAttrValueService.getSkuAttrValueListBySkuId(id);

        // 4. 获取sku图片信息
        final List<SkuImage> skuImageList = skuImageService.getSkuImageListBySkuId(id);

        BeanUtils.copyProperties(skuInfo, skuInfoVo);
        skuInfoVo.setSkuPosterList(skuPosterList);
        skuInfoVo.setSkuAttrValueList(skuAttrValueList);
        skuInfoVo.setSkuImagesList(skuImageList);

        return skuInfoVo;
    }

    @Override
    public Boolean isNewPerson(final Long skuId, final Integer status) {
        final SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsNewPerson(status);
        return baseMapper.updateById(skuInfo) > 0;
    }

    @Override
    public List<SkuInfo> findSkuInfoList(final List<Long> skuIdList) {
        if (CollectionUtils.isEmpty(skuIdList)) {
            return Collections.emptyList();
        }
        return baseMapper.selectBatchIds(skuIdList);
    }

    @Override
    public List<SkuInfo> findSkuInfoByKeyword(final String keyword) {
        final LambdaQueryWrapper<SkuInfo> skuInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        skuInfoLambdaQueryWrapper.like(SkuInfo::getSkuName, keyword);
        return baseMapper.selectList(skuInfoLambdaQueryWrapper);
    }

    @Override
    public List<SkuInfo> findNewPersonList() {

        final Page<SkuInfo> pageParam = new Page<>(1, 3);

        return baseMapper.selectPage(pageParam, Wrappers.lambdaQuery(SkuInfo.class)
                .eq(SkuInfo::getIsNewPerson, 1)
                .eq(SkuInfo::getPublishStatus, 1)
                .orderByDesc(SkuInfo::getStock)).getRecords();
    }

    @Override
    public SkuInfoVo getSkuInfoVo(final Long skuId) {

        final SkuInfoVo skuInfoVo = new SkuInfoVo();

        // 查询skuInfo基本信息
        final SkuInfo skuInfo = baseMapper.selectById(skuId);

        // 查询sku图片信息
        final List<SkuImage> skuImageList = skuImageService.getSkuImageListBySkuId(skuId);

        // 查询sku海报信息
        final List<SkuPoster> posterList = skuPosterService.getSkuPosterListBySkuId(skuId);

        // 查询sku属性信息
        final List<SkuAttrValue> skuAttrValueList = skuAttrValueService.getSkuAttrValueListBySkuId(skuId);

        BeanUtils.copyProperties(skuInfo, skuInfoVo);
        skuInfoVo.setSkuImagesList(skuImageList);
        skuInfoVo.setSkuPosterList(posterList);
        skuInfoVo.setSkuAttrValueList(skuAttrValueList);

        return skuInfoVo;
    }

    @Override
    public Boolean checkAndLock(final List<SkuStockLockVo> skuStockLockVoList, final String orderNo) {
        // 1 判断skuStockLockVoList集合是否为空
        if (CollectionUtils.isEmpty(skuStockLockVoList)) {
            throw new SsyxException(ResultCodeEnum.DATA_ERROR);
        }

        // 2 遍历skuStockLockVoList得到每一个商品，验证库存并锁定库存，具备原子性
        skuStockLockVoList.forEach(this::checkLock);

        // 3 只要有一个商品锁定失败，所有锁定成功的商品都解锁
        final boolean flag = skuStockLockVoList.stream().anyMatch(skuStockLockVo -> !skuStockLockVo.getIsLock());
        if (flag) {
            // 所有锁定成功的商品都解锁
            skuStockLockVoList.stream()
                    .filter(SkuStockLockVo::getIsLock)
                    .forEach(skuStockLockVo -> {
                        baseMapper.unlockStock(skuStockLockVo.getSkuId(), skuStockLockVo.getSkuNum());
                    });
            // 返回失败的状态值
            return Boolean.FALSE;
        }

        // 4 如果所有商品锁定成功，redis缓存相关数据，方便解锁库存与减库存
        redisTemplate.opsForValue().set(RedisConst.STOCK_INFO + orderNo, skuStockLockVoList);

        return Boolean.TRUE;
    }

    @Override
    public void minusStock(final String orderNo) {
        final List<SkuStockLockVo> skuStockLockVoList =
                (List<SkuStockLockVo>) redisTemplate.opsForValue().get(RedisConst.STOCK_INFO + orderNo);

        if (CollectionUtils.isEmpty(skuStockLockVoList)) {
            return;
        }

        skuStockLockVoList.forEach(skuStockLockVo -> {
            baseMapper.minusStock(skuStockLockVo.getSkuId(), skuStockLockVo.getSkuNum());
        });

        redisTemplate.delete(RedisConst.STOCK_INFO + orderNo);
    }

    private void checkLock(final SkuStockLockVo skuStockLockVo) {
        // 获取锁，公平锁

        final RLock rLock = this.redissonClient.getFairLock(RedisConst.SKUKEY_PREFIX + skuStockLockVo.getSkuId());
        rLock.lock();

        try {
            // 验证库存
            final SkuInfo skuInfo = baseMapper.checkStock(skuStockLockVo.getSkuId(), skuStockLockVo.getSkuNum());
            if (skuInfo == null) {
                skuStockLockVo.setIsLock(Boolean.FALSE);
                return;
            }
            // 有满足条件的商品，锁定库存
            final Integer rows = baseMapper.lockStock(skuStockLockVo.getSkuId(), skuStockLockVo.getSkuNum());
            if (rows == 1) {
                skuStockLockVo.setIsLock(Boolean.TRUE);
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            rLock.unlock();
        }
    }
}
