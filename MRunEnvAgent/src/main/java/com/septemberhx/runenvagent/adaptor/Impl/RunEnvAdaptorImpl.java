package com.septemberhx.runenvagent.adaptor.Impl;

import com.septemberhx.common.bean.agent.MDeployPodRequest;
import com.septemberhx.runenvagent.adaptor.RunEnvAdaptor;
import com.septemberhx.runenvagent.utils.K8SUtils;
import io.kubernetes.client.models.V1Pod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Lei
 * @Date 2020/7/20 19:41
 * @Version 1.0
 */

@Component
@Qualifier("RunEnvAdaptorImpl")
public class RunEnvAdaptorImpl implements RunEnvAdaptor {

    private static Logger logger = LogManager.getLogger(RunEnvAdaptorImpl.class);


    @Autowired
    private K8SUtils k8SUtils;

    @Override
    public void deployInstanceOnNode(MDeployPodRequest mDeployPodRequest) {
        try {
            V1Pod pod = this.k8SUtils.deployInstanceOnNode(
                    mDeployPodRequest.getNodeId(),
                    mDeployPodRequest.getUniqueId(),
                    mDeployPodRequest.getServiceName(),
                    mDeployPodRequest.getImageUrl()
            );
            logger.info("Job " + mDeployPodRequest.getId() + " dispatched");
        } catch (Exception e) {
            logger.warn(String.format(
                    "Failed to notify job %s to MServer. Please check the connection to MServer",
                    mDeployPodRequest.getId()
            ));
        }
    }

    @Override
    public boolean deleteInstanceById(String instanceId) {
        return this.k8SUtils.deleteInstanceById(instanceId);
    }

    @Override
    public Map<String, String> getAllnode(){
        return this.k8SUtils.getAllnode();
    }
}
