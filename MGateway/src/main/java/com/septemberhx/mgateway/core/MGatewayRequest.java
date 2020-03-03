package com.septemberhx.mgateway.core;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.config.MConfig;
import com.septemberhx.mgateway.config.MGatewayConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/3
 *
 * This class is used to solve the requests accepted in RequestController
 */
@Component
public class MGatewayRequest {

    @Autowired
    private MGatewayConfig gatewayConfig;

    private final Random random = new Random(10000);
    private static Logger logger = LogManager.getLogger(MGatewayRequest.class);

    private InstanceInfo getRandomClusterAgentInstance() {
        Application clusterAgentApp = this.gatewayConfig.getDiscoveryClient().getApplication(MConfig.MCLUSTERAGENT_NAME);
        if (clusterAgentApp != null) {
            return clusterAgentApp.getInstances().get(
                    this.random.nextInt(clusterAgentApp.getInstances().size())
            );
        }
        return null;
     }

    /*
     * Solve the request that is identified by the dependency
     * It corresponds to RequestController#dependencyRequest
     * todo: implement solveDepRequest()
     */
    public MResponse solveDepRequest(MResponse parameters) {
        return MResponse.successResponse();
    }

}
