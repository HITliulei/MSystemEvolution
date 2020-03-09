package com.septemberhx.mgateway.core;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.bean.MRoutingBean;
import com.septemberhx.common.config.MConfig;
import com.septemberhx.common.config.Mvf4msDep;
import com.septemberhx.common.service.dependency.BaseSvcDependency;
import com.septemberhx.common.utils.MRequestUtils;
import com.septemberhx.common.utils.MUrlUtils;
import com.septemberhx.mgateway.config.MGatewayConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
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
     */
    public MResponse solveDepRequest(MResponse parameters, HttpServletRequest request) {
        Mvf4msDep dep = (Mvf4msDep) parameters.get(MConfig.MGATEWAY_DEPENDENCY_ID);
        BaseSvcDependency baseSvcDependency = BaseSvcDependency.tranConfig2Dependency(dep);
        MResponse response = MResponse.failResponse();
        Optional<MRoutingBean> routingBeanOpt;
        String userId = (String) parameters.get(MConfig.MGATEWAY_CLIENT_ID);
        boolean isFromRecord = false;

        if (userId != null) {
            // user id
            MGatewayInfo.inst().recordUserDepRequest(userId, baseSvcDependency);  // record it for server to analyse
            routingBeanOpt = MGatewayInfo.inst().getRoutingFromRecordForUser(userId, baseSvcDependency);
            if (routingBeanOpt.isPresent()) {
                isFromRecord = true;
            } else {
                routingBeanOpt = MGatewayInfo.inst().getRoutingFromTableForUser(baseSvcDependency);
            }
        } else {
            // call between instances. Ip address is used to distinguish them
            routingBeanOpt = MGatewayInfo.inst().getRoutingForInst(request.getRemoteAddr(), baseSvcDependency);
        }

        if (routingBeanOpt.isPresent()) {
            if (userId != null && !isFromRecord) {  // record the new routing if not recorded before
                MGatewayInfo.inst().recordUserRouting(userId, baseSvcDependency, routingBeanOpt.get());
            }

            response = MRequestUtils.sendRequest(
                    MUrlUtils.getRemoteUri(routingBeanOpt.get()),
                    parameters,
                    MResponse.class,
                    RequestMethod.POST
            );
        }

        return response;
    }
}
