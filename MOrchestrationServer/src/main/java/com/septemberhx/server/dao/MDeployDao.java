package com.septemberhx.server.dao;

import com.netflix.appinfo.InstanceInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author Lei
 * @Date 2020/3/16 19:44
 * @Version 1.0
 */
@Getter
@Setter
public class MDeployDao {
    private String podId;
    private String registerId;
    private String nodeId;
    private String serviceName;
    private String serviceVersion;
    private String ipAddress;

    public MDeployDao() {

    }

    public MDeployDao(String podId, String nodeId, String serviceName, String serviceVersion) {
        this.podId = podId;
        this.nodeId = nodeId;
        this.serviceName = serviceName;
        this.serviceVersion = serviceVersion;
        this.registerId = null;
        this.ipAddress = null;
    }

    public MDeployDao(String podId, String registerId, String nodeId, String serviceName, String serviceVersion) {
        this.podId = podId;
        this.registerId = registerId;
        this.nodeId = nodeId;
        this.serviceName = serviceName;
        this.serviceVersion = serviceVersion;
    }

    public MDeployDao(String podId, String registerId, String nodeId, String serviceName, String serviceVersion, String ipAddress) {
        this.podId = podId;
        this.registerId = registerId;
        this.nodeId = nodeId;
        this.serviceName = serviceName;
        this.serviceVersion = serviceVersion;
        this.ipAddress = ipAddress;
    }
}