package com.septemberhx.server.model;

import com.septemberhx.common.base.MUniqueObject;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Set;


@Getter
@Setter
public class MServiceInstance extends MUniqueObject {
    // we use the pod id as the unique id of instance
    private String nodeId;                      // Shows which server node this instance belongs to
    private String ip;                          // Ip of the pod
    private Integer port;                       // Port of the pod
    private String podId;                       // The actual pod id in the K8S. It should be unique although there may have many clusters
    private String registryId;                  // Unique Id assigned by registry, e.g. Eureka
    private String serviceName;                 // Service may have different versions, and all the versions have the same service name
    private String version;                     // service version
    private String serviceId;                   // Each service version has unique service id


    public MServiceInstance(String nodeId, String ip, Integer port, String podId, String registryId, String serviceName, String version, String serviceId) {
        this.nodeId = nodeId;
        this.ip = ip;
        this.port = port;
        this.podId = podId;
        this.registryId = registryId;
        this.serviceName = serviceName;
        this.version = version;
        this.serviceId = serviceId;
    }

    @Override
    public String toString() {
        return "MServiceInstance{" +
                "nodeId='" + nodeId + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", podId='" + podId + '\'' +
                ", registryId='" + registryId + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", version='" + version + '\'' +
                ", serviceId='" + serviceId + '\'' +
                '}';
    }
}
