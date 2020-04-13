package com.septemberhx.agent.controller;

import com.netflix.appinfo.InstanceInfo;
import com.septemberhx.agent.config.MAgentConfig;
import com.septemberhx.agent.core.MRoutingInfo;
import com.septemberhx.agent.utils.MClientUtils;
import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.bean.MRoutingBean;
import com.septemberhx.common.bean.agent.MAllUserBean;
import com.septemberhx.common.bean.agent.MDepResetRoutingBean;
import com.septemberhx.common.bean.gateway.MDepReplaceRequestBean;
import com.septemberhx.common.bean.mclient.MRequestRoutingBean;
import com.septemberhx.common.bean.mclient.MUpdateSysDataBean;
import com.septemberhx.common.bean.server.MUpdateCopyInstBean;
import com.septemberhx.common.config.MConfig;
import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.MSvcInstance;
import com.septemberhx.common.utils.MRequestUtils;
import com.septemberhx.common.utils.MUrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.*;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/18
 */
@Controller
public class MDepRequestController {
    @Autowired
    private MClientUtils clientUtils;

    @Autowired
    private MAgentConfig agentConfig;

    @ResponseBody
    @PostMapping(path = MConfig.MCLUSTER_DEP_ROUTING_RESET)
    public MResponse resetRouting(@RequestBody MDepResetRoutingBean resetRoutingBean) {
        MRoutingInfo.inst().resetRoutingMap(
                resetRoutingBean.instDepMap(), resetRoutingBean.userDepMap(), resetRoutingBean.getServiceMap(), resetRoutingBean.getInstMap());

        for (InstanceInfo info : this.clientUtils.getAllGatewayInstance()) {
            URI uri = MUrlUtils.getRemoteUri(info.getIPAddr(), info.getPort(), MConfig.MGATEWAY_RESET_CACHE);
            MRequestUtils.sendRequest(uri, null, null, RequestMethod.POST);
        }

        return MResponse.successResponse();
    }

    @PostMapping(path = MConfig.MCLUSTER_DEP_REQUEST_ROUTING)
    @ResponseBody
    public MRoutingBean getRoutingBean(@RequestBody MRequestRoutingBean routingBean) {
        // finish the routing algorithm. Remember to make sure that the requests from client must be satisfied
        Optional<MRoutingBean> routingOpt = MRoutingInfo.inst().getRoutingFromRecord(
                routingBean.getClientId(), routingBean.getUserId(), routingBean.getDependency());
        if (!routingOpt.isPresent()) {
            routingOpt = MRoutingInfo.inst().findNewRoutingBean(
                    routingBean.getDependency(), routingBean.getGatewayNodeId(), routingBean.getClientId(), routingBean.getCallerPatternUrl());
            if (routingOpt.isPresent()) {
                MRoutingInfo.inst().recordRouting(
                        routingBean.getClientId(),
                        routingBean.getCallerPatternUrl(),
                        routingBean.getUserId(), routingBean.getDependency(), routingOpt.get());
                return routingOpt.get();
            }
        }
        return null;
    }

    @PostMapping(path = MConfig.MCLUSTER_UPDATE_SYS_DATA_URI)
    @ResponseBody
    public MResponse updateSysData(@RequestBody MUpdateSysDataBean dataBean) {
        Map<String, MService> svcMap = new HashMap<>();
        Map<String, MSvcInstance> instanceMap = new HashMap<>();
        for (MService svc : dataBean.getSvcList()) {
            svcMap.put(svc.getId(), svc);
        }
        for (MSvcInstance inst : dataBean.getInstList()) {
            instanceMap.put(inst.getIp(), inst);
        }

        MRoutingInfo.inst().setSvcInstanceMap(instanceMap);
        MRoutingInfo.inst().setSvcMap(svcMap);
        return MResponse.successResponse();
    }

    @ResponseBody
    @PostMapping(path = MConfig.MCLUSTER_REPLACE_CALL)
    public MResponse processReplacementRequest(@RequestBody MDepReplaceRequestBean requestBean, HttpServletRequest request) {
        return MRequestUtils.sendRequest(
                MUrlUtils.getRemoteUri(this.agentConfig.getCenter().getIp(), this.agentConfig.getCenter().getPort(), MConfig.MSERVER_REPLACE_CALL),
                requestBean,
                MResponse.class,
                RequestMethod.POST,
                createHeader(request.getHeader(MConfig.PARAM_CALLER_URL), request.getHeader(MConfig.PARAM_CALLED_URL))
        );
    }

    @ResponseBody
    @PostMapping(path = MConfig.MCLUSTER_UPDATE_COPY_MAP)
    public MResponse updateCopyInsts(@RequestBody MUpdateCopyInstBean instBean) {
        for (InstanceInfo info : this.clientUtils.getAllGatewayInstance()) {
            URI uri = MUrlUtils.getRemoteUri(info.getIPAddr(), info.getPort(), MConfig.MGATEWAY_UPDATE_COPY_MAP);
            MRequestUtils.sendRequest(uri, instBean, MResponse.class, RequestMethod.POST);
        }
        return MResponse.successResponse();
    }

    private Map<String, List<String>> createHeader(String callerUrl, String calledUrl) {
        Map<String, List<String>> customHeaders = new HashMap<>();
        List<String> p1 = new ArrayList<>();
        p1.add(callerUrl);
        List<String> p2 = new ArrayList<>();
        p2.add(calledUrl);
        customHeaders.put(MConfig.PARAM_CALLER_URL, p1);
        customHeaders.put(MConfig.PARAM_CALLED_URL, p2);
        return customHeaders;
    }
}
