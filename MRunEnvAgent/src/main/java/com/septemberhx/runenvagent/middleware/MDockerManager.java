package com.septemberhx.runenvagent.middleware;

import com.septemberhx.common.bean.agent.MDockerInfoBean;
import io.kubernetes.client.models.V1Pod;

import java.util.Map;

public interface MDockerManager {
    /**
     * get docker info by docker's ipADDr
     * @param ipAddr docker's ip
     * @return docker info
     */
    public MDockerInfoBean getDockerInfoByIpAddr(String ipAddr);

    /**
     * delete instance in cluster by instanceId
     * @param instanceId instance ID
     * @return whether delete successfully
     */
    public boolean deleteInstanceById(String instanceId);

    /**
     * deploy one instance in cluster
     * @param nodeId worker node-label
     * @param instanceId pod Id
     * @param serviceName service Name
     * @param imageUrl they service docker image url
     * @return V1Pod
     */
    public V1Pod deployInstanceOnNode(String nodeId, String instanceId, String serviceName, String imageUrl);

    /**
     *  check if this docker running in cluster
     * @param ipAddr the ip Addr of this docker
     * @return true/false
     */
    public boolean checkIfDockerRunning(String ipAddr);

    /**
     * get all node in K8S cluster
     * @return  node label
     */
    public Map<String, String> getAllnode();
}
