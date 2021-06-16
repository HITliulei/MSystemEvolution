package com.septemberhx.common.service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author Lei
 * @Date 2020/2/10 10:35
 * @Version 1.0
 */
@Getter
@Setter
@ToString
public class MDenpend {
    private String serviceName;
    private String serviceVersion;
    private String dependServiceName;
    private String dependServiceVersion;

    public MDenpend() {

    }

    public MDenpend(String serviceName, String serviceVersion, String dependServiceName, String dependServiceVersion) {
        this.serviceName = serviceName;
        this.serviceVersion = serviceVersion;
        this.dependServiceName = dependServiceName;
        this.dependServiceVersion = dependServiceVersion;
    }
}
