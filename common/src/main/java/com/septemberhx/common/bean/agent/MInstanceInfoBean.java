package com.septemberhx.common.bean.agent;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * MInstanceInfoBean
 *
 * @author septemberhx
 * @date 2019-06-15
 */

@Getter
@Setter
public class MInstanceInfoBean {
    private String registryId;          // Unique id that the register server assigns to it
    private String ip;
    private int port;
    private String clusterId;
    private String version;
    private String serviceName;
    /**
     * to build the topology
     */
    private Map<String, String> parentIdMap = new HashMap<>();
    private Map<String, Set<String>> apiMap = new HashMap<>();
    private Set<String> mObjectIdMap = new HashSet<>();
    private MDockerInfoBean dockerInfo;

    @Override
    public String toString() {
        return "MInstanceInfoBean{" +
                "registryId='" + registryId + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", clusterId='" + clusterId + '\'' +
                ", version='" + version + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", parentIdMap=" + parentIdMap +
                ", apiMap=" + apiMap +
                ", mObjectIdMap=" + mObjectIdMap +
                ", dockerInfo=" + dockerInfo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MInstanceInfoBean infoBean = (MInstanceInfoBean) o;

        if (!Objects.equals(this.registryId, infoBean.registryId) || !Objects.equals(this.ip, infoBean.ip)
                || !Objects.equals(this.port, infoBean.port) || !Objects.equals(this.clusterId, infoBean.clusterId)
                || !Objects.equals(this.serviceName, infoBean.serviceName))
            return false;

        if (this.parentIdMap.size() != infoBean.parentIdMap.size()) return false;
        if (!this.parentIdMap.keySet().containsAll(infoBean.parentIdMap.keySet())) return false;

        if (this.apiMap.size() != infoBean.apiMap.size()) return false;
        if (!this.apiMap.keySet().containsAll(infoBean.apiMap.keySet())) return false;
        for (String key : this.apiMap.keySet()) {
            Set<String> set1 = this.apiMap.get(key);
            Set<String> set2 = infoBean.apiMap.get(key);
            if (set1.size() != set2.size() || !set1.containsAll(set2)) return false;
        }

        if (this.mObjectIdMap.size() != infoBean.mObjectIdMap.size() || this.mObjectIdMap.containsAll(infoBean.mObjectIdMap)) return false;

        return Objects.equals(this.dockerInfo, infoBean.dockerInfo);
    }
}
