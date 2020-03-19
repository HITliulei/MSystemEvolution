package com.septemberhx.common.service;

import com.septemberhx.common.base.MUniqueObject;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Set;


@Getter
@Setter
public class MSvcInstance extends MUniqueObject {

    // we use the pod id as the unique id of instance

    private String clusterId;                   // Shows which cluster this instance belongs to
    private String nodeId;                      // Shows which server node this instance belongs to
    private String ip;                          // Ip of the pod
    private Integer port;                       // Port of the pod
    private String podId;                       // The actual pod id in the K8S. It should be unique although there may have many clusters
                                                //   this.podId should equals this.id
    private String registryId;                  // Unique Id assigned by registry, e.g. Eureka

    private String serviceName;                 // Service may have different versions, and all the versions have the same service name
    private String version;                     // service version
    private String serviceId;                   // Each service version has unique service id

    private Set<String> mObjectIdSet;           // For the possible future works. Not used for now
    private Map<String, String> parentIdMap;    // For the possible future works. Not used for now

    public MSvcInstance(Map<String, String> parentIdMap, String clusterId, String nodeId, String ip, Integer port,
                        String instanceId, Set<String> mObjectIdSet, String serviceName, String serviceId, String registryId, String version) {
        this.parentIdMap = parentIdMap;
        this.clusterId = clusterId;
        this.nodeId = nodeId;
        this.ip = ip;
        this.port = port;
        this.id = instanceId;
        this.mObjectIdSet = mObjectIdSet;
        this.serviceName = serviceName;
        this.serviceId = serviceId;
        this.podId = instanceId;        // We use the unique instance id as the pod id
                                        // Thus only a-z,A-Z,-,0-9 should be used as the instance id
        this.registryId = registryId;
        this.version = version;
    }

    public MSvcInstance deepClone() {
        return new MSvcInstance(
                this.parentIdMap,
                this.clusterId,
                this.nodeId,
                this.ip,
                this.port,
                this.id,
                this.mObjectIdSet,
                this.serviceName,
                this.serviceId,
                this.registryId,
                this.version
        );
    }

    @Override
    public String toString() {
        return "MServiceInstance{" +
                "clusterId='" + clusterId + '\'' +
                ", nodeId='" + nodeId + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", podId='" + podId + '\'' +
                ", registryId='" + registryId + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", version='" + version + '\'' +
                ", serviceId='" + serviceId + '\'' +
                ", mObjectIdSet=" + mObjectIdSet +
                ", parentIdMap=" + parentIdMap +
                ", id='" + id + '\'' +
                '}';
    }
}
