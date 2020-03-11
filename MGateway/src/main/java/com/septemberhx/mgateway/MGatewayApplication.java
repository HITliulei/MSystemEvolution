package com.septemberhx.mgateway;

import com.septemberhx.mgateway.core.MRequestProcessorThread;
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
        new Thread(new MRequestProcessorThread(), MRequestProcessorThread.class.getSimpleName()).start();
        SpringApplication.run(MGatewayApplication.class, args);
    }
}
