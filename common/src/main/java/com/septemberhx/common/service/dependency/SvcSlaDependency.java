package com.septemberhx.common.service.dependency;

import lombok.Getter;
import lombok.ToString;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/2/29
 */
@Getter
@ToString
public class SvcSlaDependency extends BaseSvcDependency {

    // service name
    private String serviceName;

    // prefer sla level
    private int slaLevel;

    // API url
    private String patternUrl;

    public SvcSlaDependency(String id, String serviceName, int slaLevel, String patternUrl) {
        this.id = id;
        this.serviceName = serviceName;
        this.slaLevel = slaLevel;
        this.patternUrl = patternUrl;
    }
}
