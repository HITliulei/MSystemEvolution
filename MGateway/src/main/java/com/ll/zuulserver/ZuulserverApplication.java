package com.ll.zuulserver;

import com.ll.zuulserver.routing.Routing;
import com.netflix.zuul.ZuulFilter;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableZuulProxy
@SpringBootApplication
public class ZuulserverApplication {


    @Bean
    public ZuulFilter routingFilter(RouteLocator routeLocator){
        return new Routing(routeLocator);
    }

    public static void main(String[] args) {
        SpringApplication.run(ZuulserverApplication.class, args);
    }

}
