package com.micro.ssyx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author zhaochongru.sh@amassfreight.com
 * @description
 * @date 2024/5/13 21:48
 */
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class ServiceActivityApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ServiceActivityApplication.class, args);
    }
}
