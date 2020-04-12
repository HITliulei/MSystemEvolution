package com.septemberhx.mgateway.controller;

import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.bean.MTimeIntervalBean;
import com.septemberhx.common.bean.gateway.MDepRequestCacheBean;
import com.septemberhx.common.bean.gateway.MDepRequestCacheListBean;
import com.septemberhx.common.bean.gateway.MDepRequestCountBean;
import com.septemberhx.common.bean.server.MUpdateCopyInstBean;
import com.septemberhx.common.config.MConfig;
import com.septemberhx.common.service.dependency.PureSvcDependency;
import com.septemberhx.mgateway.config.MGatewayConfig;
import com.septemberhx.mgateway.core.MGatewayInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/9
 */
@Controller
public class EvolveController {

    @Autowired
    private MGatewayConfig gatewayConfig;

    public MResponse updateRoutingTable() {
        // todo
        return MResponse.successResponse();
    }

    @ResponseBody
    @PostMapping(path = MConfig.MGATEWAY_FETCH_REQUESTS)
    public MDepRequestCacheListBean getRequestBetweenTime(@RequestBody MTimeIntervalBean timeIntervalBean) {
        return new MDepRequestCacheListBean(MGatewayInfo.inst().getRequestBetweenTime(
                timeIntervalBean.getStartTimeInMills(), timeIntervalBean.getEndTimeInMills()
        ));
    }

    @ResponseBody
    @PostMapping(path = MConfig.MGATEWAY_FETCH_REQUEST_NUMBER)
    public MDepRequestCountBean getRequestCountBetweenTime(@RequestBody MTimeIntervalBean intervalBean) {
        MDepRequestCountBean requestCountBean = new MDepRequestCountBean(this.gatewayConfig.getNodeId());
        Map<PureSvcDependency, Set<String>> depUserSet = new HashMap<>();
        for (MDepRequestCacheBean cacheBean :
                MGatewayInfo.inst().getRequestBetweenTime(intervalBean.getStartTimeInMills(), intervalBean.getEndTimeInMills())) {
            PureSvcDependency dep = cacheBean.getBaseSvcDependency().getDep();
            if (!depUserSet.containsKey(dep)) {
                depUserSet.put(dep, new HashSet<>());
            }
            depUserSet.get(dep).add(cacheBean.getClientId());
        }

        for (PureSvcDependency dep : depUserSet.keySet()) {
            requestCountBean.putValue(dep, depUserSet.get(dep).size());
        }
        return requestCountBean;
    }

    @ResponseBody
    @PostMapping(path = MConfig.MGATEWAY_UPDATE_COPY_MAP)
    public MResponse updateCopyInsts(@RequestBody MUpdateCopyInstBean instBean) {
        MGatewayInfo.inst().setReplaceMap(instBean.getCopyMap());
        return MResponse.successResponse();
    }

    @ResponseBody
    @PostMapping(path = MConfig.MGATEWAY_FAILED_REQUESTS)
    public List<MDepRequestCacheBean> failedRequests() {
        return MGatewayInfo.inst().faildRequests();
    }
}
