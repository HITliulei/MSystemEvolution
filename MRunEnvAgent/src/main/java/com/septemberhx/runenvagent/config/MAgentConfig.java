package com.septemberhx.runenvagent.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/4/10
 */
@EnableConfigurationProperties
@Configuration
@ConfigurationProperties(prefix = "mvf4ms")
@Getter
@Setter
@ToString
public class MAgentConfig {
    private MIpAndPortConfig center;
    private MClusterConfig cluster;
    private MIpAndPortConfig elasticsearch;
}
