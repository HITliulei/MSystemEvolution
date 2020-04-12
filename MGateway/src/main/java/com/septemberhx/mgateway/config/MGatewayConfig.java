package com.septemberhx.mgateway.config;

import com.netflix.discovery.EurekaClient;
import com.septemberhx.mgateway.core.MRequestProcessorThread;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/3
 */
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "mvf4ms.gateway")
@Configuration
@Getter
@Setter
public class MGatewayConfig {

    @Qualifier("eurekaClient")
    @Autowired
    private EurekaClient discoveryClient;

    private String nodeId;
}
