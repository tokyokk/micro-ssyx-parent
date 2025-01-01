package com.micro.ssyx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author micro
 * @description
 * @date 2024/7/8 15:10
 * @github https://github.com/tokyokk
 */
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class ServiceOrderApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ServiceOrderApplication.class, args);
    }
}
