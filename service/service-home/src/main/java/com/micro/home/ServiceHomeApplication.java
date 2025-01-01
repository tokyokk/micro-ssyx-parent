package com.micro.home;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author zhaochongru.sh@amassfreight.com
 * @description
 * @date 2024/6/24 20:02
 */
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ServiceHomeApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ServiceHomeApplication.class, args);
    }
}
