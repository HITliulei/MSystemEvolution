package com.septemberhx.mgateway.controller;

import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.bean.MTimeIntervalBean;
import com.septemberhx.common.bean.gateway.MDepRequestCacheBean;
import com.septemberhx.common.bean.gateway.MDepRequestCacheListBean;
import com.septemberhx.common.config.MConfig;
import com.septemberhx.mgateway.core.MGatewayInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/9
 */
@Controller
public class EvolveController {

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
}
