package com.septemberhx.common.bean.agent;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@ToString
public class MDockerInfoBean {
    private String hostIp;
    private String nodeLabel;
    private String instanceId;      // instance id that k8s assigns to it

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MDockerInfoBean that = (MDockerInfoBean) o;
        return Objects.equals(hostIp, that.hostIp) &&
                Objects.equals(instanceId, that.instanceId) &&
                Objects.equals(nodeLabel, that.nodeLabel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostIp, instanceId, nodeLabel);
    }
}
