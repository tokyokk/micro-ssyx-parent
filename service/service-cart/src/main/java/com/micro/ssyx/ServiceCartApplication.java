package com.micro.ssyx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author micro
 * @description 购物车模块启动类
 * @date 2024/7/5 16:36
 * @github https://github.com/tokyokk
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableFeignClients
@EnableDiscoveryClient
public class ServiceCartApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ServiceCartApplication.class, args);
    }
}
