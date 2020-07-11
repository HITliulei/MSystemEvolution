package com.septemberhx.runenvagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @Author Lei
 * @Date 2020/7/11 19:57
 * @Version 1.0
 */
@SpringBootApplication
@EnableEurekaClient
public class MRunEnvAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(MRunEnvAgentApplication.class,args);
    }
}
