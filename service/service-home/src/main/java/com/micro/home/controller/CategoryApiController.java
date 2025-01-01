package com.micro.home.controller;

import com.micro.ssyx.client.product.ProductFeignClient;
import com.micro.ssyx.common.result.ResultResponse;
import com.micro.ssyx.model.product.Category;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author micro
 * @description 商品分类控制器
 * @date 2024/7/2 00:01
 * @github https://github.com/tokyokk
 */
@RequestMapping("/api/home")
@RestController
public class CategoryApiController {

    @Resource
    private ProductFeignClient productFeignClient;

    @GetMapping("product")
    public ResultResponse<List<Category>> categoryList() {
        return ResultResponse.ok(productFeignClient.findAllCategoryList());
    }
}
