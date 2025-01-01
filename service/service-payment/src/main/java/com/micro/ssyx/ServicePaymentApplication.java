package com.micro.ssyx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author micro
 * @description
 * @date 2024/7/8 19:10
 * @github https://github.com/tokyokk
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class ServicePaymentApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ServicePaymentApplication.class, args);
    }
}
