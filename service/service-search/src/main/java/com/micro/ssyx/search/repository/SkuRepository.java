package com.micro.ssyx.search.repository;

import com.micro.ssyx.model.search.SkuEs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author micro
 * @description
 * @date 2024/5/6 20:49
 * @github https://github.com/microsbug
 */
public interface SkuRepository extends ElasticsearchRepository<SkuEs, Long> {

    /**
     * 查询爆品商品数据
     *
     * @param pageable 分页信息
     * @return 爆款商品分页数据
     */
    Page<SkuEs> findByOrderByHotScoreDesc(Pageable pageable);

    /**
     * 根据分类id和仓库id查询商品数据
     *
     * @param categoryId 分类id
     * @param wareId     仓库id
     * @param pageable   分页信息
     * @return 商品分页数据
     */
    Page<SkuEs> findByCategoryIdAndWareId(Long categoryId, Long wareId, Pageable pageable);

    /**
     * 根据关键字和仓库id查询商品数据
     *
     * @param keyword  关键字
     * @param wareId   仓库id
     * @param pageable 分页信息
     * @return 商品分页数据
     */
    Page<SkuEs> findByKeywordAndWareId(String keyword, Long wareId, Pageable pageable);
}
