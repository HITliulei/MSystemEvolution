package com.septemberhx.mclient.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/1
 */
@Getter
@Setter
@ToString
public class Mvf4msDep {
    private String id;
    private String serviceName;
    private String patternUrl;
    private String version;
    private String function;
    private String sla;
}
