package com.septemberhx.mgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
//import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/2
 */

@SpringBootApplication
@EnableZuulProxy
//@EnableFeignClients
public class MGatewayApplication {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(MGatewayApplication.class, args);
    }
}
