package com.micro.ssyx.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MybatisPlus配置类
 */
@EnableTransactionManagement
@Configuration
@MapperScan("com.micro.ssyx.*.mapper")
public class MybatisPlusConfig {

    /**
     * mybatis-plus分页插件
     */
    @Bean
    public MybatisPlusInterceptor optimisticLockerInnerInterceptor() {
        final MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 向Mybatis过滤器链中添加分页拦截器
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}