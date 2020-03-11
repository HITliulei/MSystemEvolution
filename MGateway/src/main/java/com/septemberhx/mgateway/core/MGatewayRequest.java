package com.septemberhx.mgateway.core;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.bean.MRoutingBean;
import com.septemberhx.common.bean.gateway.MDepRequestCacheBean;
import com.septemberhx.common.config.MConfig;
import com.septemberhx.common.service.dependency.BaseSvcDependency;
import com.septemberhx.common.utils.MRequestUtils;
import com.septemberhx.common.utils.MUrlUtils;
import com.septemberhx.mgateway.config.MGatewayConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.net.URI;
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
     * Solve the request that is identified by the dependency from instances
     * It corresponds to RequestController#dependencyRequest
     */
    public MResponse solveInstDepRequest(String instanceIp, BaseSvcDependency dependency, MResponse parameters) {
        Optional<MRoutingBean> routingBeanOpt = MGatewayInfo.inst().getRoutingForInst(instanceIp, dependency);
        MResponse response = MResponse.failResponse();
        if (routingBeanOpt.isPresent()) {
            response = MRequestUtils.sendRequest(
                    MUrlUtils.getRemoteUri(routingBeanOpt.get()),
                    parameters,
                    MResponse.class,
                    RequestMethod.POST
            );
        }
        return response;
    }

    /*
     * Solve the request that is identified by the dependency from users
     * It corresponds to MRequestProcessorThread
     */
    public boolean solveUserDepRequest(MDepRequestCacheBean requestCacheBean) {
        String userId = requestCacheBean.getClientId();
        BaseSvcDependency dependency = requestCacheBean.getBaseSvcDependency();
        MResponse parameters = requestCacheBean.getParameters();

        MGatewayInfo.inst().recordUserDepRequest(requestCacheBean);  // record it for server to analyse
        Optional<MRoutingBean> routingBeanOpt = MGatewayInfo.inst().getRoutingFromRecordForUser(userId, dependency);
        boolean isFromRecord = false;
        if (routingBeanOpt.isPresent()) {
            isFromRecord = true;
        } else {
            routingBeanOpt = MGatewayInfo.inst().getRoutingFromTableForUser(dependency);
        }

        if (routingBeanOpt.isPresent()) {
            if (!isFromRecord) {
                MGatewayInfo.inst().recordUserRouting(userId, dependency, routingBeanOpt.get());
            }

            MResponse response = MRequestUtils.sendRequest(
                    MUrlUtils.getRemoteUri(routingBeanOpt.get()),
                    parameters,
                    MResponse.class,
                    RequestMethod.POST
            );

            try {
                URI uri = new URI((String) parameters.get(MConfig.MGATEWAY_CALL_BACK_URL_ID));
                MRequestUtils.sendRequest(uri, response, null, RequestMethod.POST);
            } catch (Exception e) {
                logger.debug(String.format(
                        "Illegal call back url for request from user %s with dependency %s", userId, dependency.getId())
                );
            }
            return true;
        } else {
            MGatewayInfo.inst().recordCannotSatisfiedRequest(requestCacheBean);
            return false;
        }
    }
}
