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
    // Unique id that the register server assigns to it
    private String registryId;
    private String ip;
    private Integer port;
    private String serviceName;
    private String serviceVersion;
    private MDockerInfoBean dockerInfo;
    /**
     * to build the topology
     */

    @Override
    public String toString() {
        return "MInstanceInfoBean{" +
                "registryId='" + registryId + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", version='" + serviceVersion + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", dockerInfo=" + dockerInfo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        MInstanceInfoBean infoBean = (MInstanceInfoBean) o;

        if (!Objects.equals(this.registryId, infoBean.registryId) || !Objects.equals(this.ip, infoBean.ip)
                || !Objects.equals(this.port, infoBean.port)
                || !Objects.equals(this.serviceName, infoBean.serviceName))
        {return false;}

        return Objects.equals(this.dockerInfo, infoBean.dockerInfo);
    }
}
