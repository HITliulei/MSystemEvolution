package com.ll.mbuildcenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author Lei
 * @Date 2020/2/15 20:18
 * @Version 1.0
 */

@SpringBootApplication
@EnableFeignClients
@EnableEurekaClient
public class MBuildCenterMain {

    public static void main(String[] args) {
        SpringApplication.run(MBuildCenterMain.class, args);
    }
}
