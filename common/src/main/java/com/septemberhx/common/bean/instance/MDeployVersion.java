package com.septemberhx.common.bean.instance;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author Lei
 * @Date 2020/3/16 14:44
 * @Version 1.0
 */
@Getter
@Setter
@ToString
public class MDeployVersion {
    private String serviceName;
    private String serviceVersion;
    private String nodeid;

    public MDeployVersion(String serviceName, String serviceVersion, String nodeid) {
        this.serviceName = serviceName;
        this.serviceVersion = serviceVersion;
        this.nodeid = nodeid;
    }

    public MDeployVersion() {
    }
}
