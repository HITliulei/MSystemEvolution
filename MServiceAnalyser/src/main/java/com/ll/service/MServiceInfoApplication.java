package com.ll.service;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class MServiceInfoApplication {
    @Bean
    @LoadBalanced
    public RestTemplate get(){
        return new RestTemplate();
    }
    public static void main(String[] args) {
        SpringApplication.run(MServiceInfoApplication.class, args);
    }

}
