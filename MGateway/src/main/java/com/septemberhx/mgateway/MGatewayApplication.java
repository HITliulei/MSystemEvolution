package com.septemberhx.mgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/2
 */
@EnableEurekaClient
@SpringBootApplication
public class MGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(MGatewayApplication.class, args);
    }
}
