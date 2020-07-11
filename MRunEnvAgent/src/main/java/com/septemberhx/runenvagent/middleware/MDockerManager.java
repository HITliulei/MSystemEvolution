package com.septemberhx.runenvagent.middleware;

import com.septemberhx.common.bean.agent.MDockerInfoBean;
import io.kubernetes.client.models.V1Pod;

public interface MDockerManager {
    /**
     *
     * @param ipAddr
     * @return
     */
    public MDockerInfoBean getDockerInfoByIpAddr(String ipAddr);
    public boolean deleteInstanceById(String instanceId);
    public V1Pod deployInstanceOnNode(String nodeId, String instanceId, String serviceName, String imageUrl);
    public boolean checkIfDockerRunning(String ipAddr);
}
