package com.micro.ssyx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.micro")
@SpringBootApplication
@EnableDiscoveryClient
public class ServiceAclApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ServiceAclApplication.class, args);
    }
}
