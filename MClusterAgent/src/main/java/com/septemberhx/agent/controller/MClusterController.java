package com.septemberhx.agent.controller;

import com.septemberhx.agent.config.MAgentConfig;
import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.bean.agent.MRegisterClusterBean;
import com.septemberhx.common.config.MConfig;
import com.septemberhx.common.utils.MRequestUtils;
import com.septemberhx.common.utils.MUrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/4/10
 */
@RestController
@RequestMapping(value = "/cluster")
public class MClusterController {

    @Autowired
    private MAgentConfig agentConfig;

    @ResponseBody
    @PostMapping(value = "/register")
    public MResponse registerCluster(@RequestBody MRegisterClusterBean clusterBean) {
        return MRequestUtils.sendRequest(
                MUrlUtils.getRemoteUri(agentConfig.getCenter().getIp(),
                        agentConfig.getCenter().getPort(), MConfig.MSERVER_CLUSTER_REGISTER),
                clusterBean,
                MResponse.class,
                RequestMethod.POST
        );
    }
}
