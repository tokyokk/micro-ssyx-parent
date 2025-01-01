package com.micro.ssyx;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author micro
 * @description 商品搜索模块启动类
 * @date 2024/5/6 20:43
 * @github https://github.com/microsbug
 */
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)// 取消数据源自动配置
public class ServiceSearchApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ServiceSearchApplication.class, args);
    }
}
