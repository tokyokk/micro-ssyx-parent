package com.micro.ssyx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author zhaochongru.sh@amassfreight.com
 * @description
 * @date 2024/5/22 20:23
 */
@EnableDiscoveryClient
@SpringBootApplication
public class ServiceGatewayApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ServiceGatewayApplication.class, args);
    }
}
