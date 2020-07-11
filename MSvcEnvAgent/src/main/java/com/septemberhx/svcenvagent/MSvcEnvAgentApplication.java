package com.septemberhx.svcenvagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/7/11
 */
@SpringBootApplication
@EnableEurekaClient
public class MSvcEnvAgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(MSvcEnvAgentApplication.class, args);
    }
}
