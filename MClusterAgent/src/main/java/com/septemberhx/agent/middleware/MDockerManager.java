package com.septemberhx.agent.middleware;

import com.septemberhx.common.bean.agent.MDockerInfoBean;
import io.kubernetes.client.models.V1Pod;

import java.util.Map;

public interface MDockerManager {
    public MDockerInfoBean getDockerInfoByIpAddr(String ipAddr);
    public boolean deleteInstanceById(String instanceId);
    public V1Pod deployInstanceOnNode(String nodeId, String instanceId, String serviceName, String imageUrl);
    public boolean checkIfDockerRunning(String ipAddr);
    public Map<String, String> getAllnode();
}
