package com.septemberhx.agent.controller;

import com.septemberhx.agent.core.MRoutingInfo;
import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.bean.MRoutingBean;
import com.septemberhx.common.bean.mclient.MRequestRoutingBean;
import com.septemberhx.common.bean.mclient.MUpdateSysDataBean;
import com.septemberhx.common.config.MConfig;
import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.MSvcInstance;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/18
 */
@Controller
public class MDepRequestController {

    @PostMapping(path = MConfig.MCLUSTER_DEP_REQUEST_ROUTING)
    @ResponseBody
    public MRoutingBean getRoutingBean(@RequestBody MRequestRoutingBean routingBean) {
        // finish the routing algorithm. Remember to make sure that the requests from client must be satisfied
        Optional<MRoutingBean> routingOpt = MRoutingInfo.inst().getRoutingFromRecord(
                routingBean.getClientId(), routingBean.getUserId(), routingBean.getDependency());
        if (!routingOpt.isPresent()) {
            routingOpt = MRoutingInfo.inst().findNewRoutingBean(routingBean.getDependency());
            routingOpt.ifPresent(mRoutingBean -> MRoutingInfo.inst().recordRouting(
                    routingBean.getClientId(),
                    routingBean.getCallerPatternUrl(),
                    routingBean.getUserId(), routingBean.getDependency(), mRoutingBean));
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
}
