package com.septemberhx.runenvagent.adaptor;

import com.septemberhx.common.bean.agent.MDeployPodRequest;

import java.util.Map;

/**
 * @Author Lei
 * @Date 2020/7/20 17:42
 * @Version 1.0
 */
public interface RunEnvAdaptor {

    /**
     * deploy node
     * @param mDeployPodRequest deploy info
     * @return V1Pod
     */
    void deployInstanceOnNode(MDeployPodRequest mDeployPodRequest);


    /**
     * delete instance in cluster by instanceId
     * @param instanceId instance ID
     * @return whether delete successfully
     */
    boolean deleteInstanceById(String instanceId);


    /**
     * get all node in K8S cluster
     * @return  node label
     */
    Map<String, String> getAllnode();



}
