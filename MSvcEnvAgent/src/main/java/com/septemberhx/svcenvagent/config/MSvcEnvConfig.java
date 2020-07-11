package com.septemberhx.svcenvagent.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/7/11
 */
@EnableConfigurationProperties
@Configuration
@ConfigurationProperties(prefix = "mvf4ms.svcenv")
@Getter
@Setter
@ToString
public class MSvcEnvConfig {
    private String notifySvcName;       // which service should be notified when instance status are changed
}
