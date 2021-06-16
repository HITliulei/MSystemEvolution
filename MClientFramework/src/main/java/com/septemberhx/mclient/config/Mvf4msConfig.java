package com.septemberhx.mclient.config;

import com.septemberhx.common.config.Mvf4msDep;
import com.septemberhx.common.config.Mvf4msDepConfig;
import com.septemberhx.mclient.controller.MClientController;
import com.septemberhx.mclient.utils.MGatewayRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/1
 */
@EnableConfigurationProperties
@Configuration
@ConfigurationProperties(prefix = "mvf4ms")
@Getter
@Setter
@ToString
public class Mvf4msConfig {
    private String version;
    private List<Mvf4msDepConfig> dependencies;

    @Bean
    public MClientController mClientController() {
        return new MClientController();
    }

    @Bean
    public MGatewayRequest mSendRequest(){
        return new MGatewayRequest();
    }

    @Bean
    @LoadBalanced
    RestTemplate initRestTemplate() {
        return new RestTemplate();
    }

    /*
     * Please note that there may have multi dep with the same dep id for one request
     */
    public List<Mvf4msDep> getDepListById(String depId) {
        List<Mvf4msDep> result = new ArrayList<>();
        for (Mvf4msDepConfig depConfig : this.dependencies) {
            depConfig.getDepById(depId).ifPresent(result::add);
        }

        return result;
    }
}
