package com.septemberhx.mgateway.core;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.bean.MRoutingBean;
import com.septemberhx.common.bean.gateway.MDepReplaceRequestBean;
import com.septemberhx.common.bean.gateway.MDepRequestCacheBean;
import com.septemberhx.common.bean.mclient.MRequestRoutingBean;
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
import java.util.*;

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

    /**
     * Ask the gateway to get the routing
     * @param clientId: who send the requests. It may be a service instance or user client
     * @param dep: dependency description
     * @param userId: which user need this
     */
     public Optional<MRoutingBean> askClusterAgentForRoutingBean(String clientId, BaseSvcDependency dep, String userId) {
         InstanceInfo agentInfo = this.getRandomClusterAgentInstance();
         if (agentInfo != null) {
             URI uri = MUrlUtils.getRemoteUri(agentInfo.getIPAddr(), agentInfo.getPort(), MConfig.MCLUSTERAGENT_REQUEST_REMOTE_URI);
             MRoutingBean result = MRequestUtils.sendRequest(
                     uri, new MRequestRoutingBean(clientId, userId, dep), MRoutingBean.class, RequestMethod.POST);
             if (result.getIpAddr() != null) {
                 return Optional.of(result);
             }
         }
         return Optional.empty();
     }

    /*
     * Solve the request that is identified by the dependency from instances
     * It corresponds to RequestController#dependencyRequest
     */
    public MResponse solveInstDepRequest(String instanceIp, BaseSvcDependency dependency, MResponse parameters, String calledUrl, String userId) {
        Optional<MRoutingBean> routingBeanOpt = MGatewayInfo.inst().getRouting(instanceIp, dependency, userId);
        MResponse response = MResponse.failResponse();

        if (!routingBeanOpt.isPresent()) {
            routingBeanOpt = this.askClusterAgentForRoutingBean(instanceIp, dependency, userId);
            routingBeanOpt.ifPresent(routingBean -> MGatewayInfo.inst().cacheRouting(instanceIp, dependency, userId, routingBean));
        }

        if (routingBeanOpt.isPresent()) {
            logger.info(String.format("Routing: %s", routingBeanOpt.get()));
            Optional<String> replaceOpt = MGatewayInfo.inst().getReplacement(routingBeanOpt.get().getIpAddr());
            if (replaceOpt.isPresent()) {
                InstanceInfo agentInfo = this.getRandomClusterAgentInstance();
                if (agentInfo != null) {
                    response = MRequestUtils.sendRequest(
                            MUrlUtils.getRemoteUri(agentInfo.getIPAddr(), agentInfo.getPort(), MConfig.MCLUSTER_REPLACE_CALL),
                            new MDepReplaceRequestBean(parameters, routingBeanOpt.get(), replaceOpt.get()),
                            MResponse.class,
                            RequestMethod.POST,
                            createHeader(calledUrl, routingBeanOpt.get().getPatternUrl(), userId)
                    );
                }
            } else {
                response = MRequestUtils.sendRequest(
                        MUrlUtils.getRemoteUri(routingBeanOpt.get()),
                        parameters,
                        MResponse.class,
                        RequestMethod.POST,
                        createHeader(calledUrl, routingBeanOpt.get().getPatternUrl(), userId)
                );
            }
        } else {
            logger.warn(String.format("Cannot response to request from %s with dep %s", instanceIp, dependency));
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
        MResponse response = MResponse.failResponse();

        MGatewayInfo.inst().recordUserDepRequest(requestCacheBean);  // record it for server to analyse
        Optional<MRoutingBean> routingBeanOpt = MGatewayInfo.inst().getRouting(userId, dependency, userId);
        if (!routingBeanOpt.isPresent()) {
            routingBeanOpt = this.askClusterAgentForRoutingBean(userId, dependency, userId);
            routingBeanOpt.ifPresent(routingBean -> MGatewayInfo.inst().cacheRouting(userId, dependency, userId, routingBean));
        }

        if (routingBeanOpt.isPresent()) {
            Optional<String> replaceOpt = MGatewayInfo.inst().getReplacement(routingBeanOpt.get().getIpAddr());
            if (replaceOpt.isPresent()) {
                InstanceInfo agentInfo = this.getRandomClusterAgentInstance();
                if (agentInfo != null) {
                    response = MRequestUtils.sendRequest(
                            MUrlUtils.getRemoteUri(agentInfo.getIPAddr(), agentInfo.getPort(), MConfig.MCLUSTER_REPLACE_CALL),
                            new MDepReplaceRequestBean(parameters, routingBeanOpt.get(), replaceOpt.get()),
                            MResponse.class,
                            RequestMethod.POST,
                            createHeader(null, routingBeanOpt.get().getPatternUrl(), userId)
                    );
                }
            } else {
                response = MRequestUtils.sendRequest(
                        MUrlUtils.getRemoteUri(routingBeanOpt.get()),
                        parameters,
                        MResponse.class,
                        RequestMethod.POST,
                        createHeader(null, routingBeanOpt.get().getPatternUrl(), userId)
                );
            }

            try {
                URI uri = new URI((String) parameters.get(MConfig.MGATEWAY_CALL_BACK_URL_ID));
                MRequestUtils.sendRequest(uri, response, null, RequestMethod.POST);
            } catch (Exception e) {
                logger.info(String.format(
                        "Illegal call back url for request from user %s with dependency %s", userId, dependency.getId())
                );
            }
            return true;
        } else {
            MGatewayInfo.inst().recordCannotSatisfiedRequest(requestCacheBean);
            return false;
        }
    }

    public Map<String, List<String>> createHeader(String callerUrl, String calledUrl, String userId) {
        Map<String, List<String>> customHeaders = new HashMap<>();
        List<String> p1 = new ArrayList<>();
        p1.add(callerUrl);
        List<String> p2 = new ArrayList<>();
        p2.add(calledUrl);
        List<String> p3 = new ArrayList<>();
        p3.add(userId);
        customHeaders.put(MConfig.PARAM_CALLER_URL, p1);
        customHeaders.put(MConfig.PARAM_CALLED_URL, p2);
        customHeaders.put(MConfig.PARAM_USER_ID, p3);
        return customHeaders;
    }
}
