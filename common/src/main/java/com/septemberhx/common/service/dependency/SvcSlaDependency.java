package com.septemberhx.common.service.dependency;

import lombok.Getter;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/2/29
 */
@Getter
public class SvcSlaDependency extends BaseSvcDependency {

    // service name
    private String serviceName;

    // prefer sla level
    private int slaLevel;

    // API url
    private String patternUrl;

    public SvcSlaDependency(String name, String serviceName, int slaLevel, String patternUrl) {
        this.name = name;
        this.serviceName = serviceName;
        this.slaLevel = slaLevel;
        this.patternUrl = patternUrl;
    }

    @Override
    public String toString() {
        return "SvcSlaDependency{" +
                "serviceName='" + serviceName + '\'' +
                ", slaLevel=" + slaLevel +
                ", patternUrl='" + patternUrl + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
