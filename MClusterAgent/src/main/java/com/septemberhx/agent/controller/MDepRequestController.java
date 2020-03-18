package com.septemberhx.agent.controller;

import com.septemberhx.common.bean.MRoutingBean;
import com.septemberhx.common.bean.mclient.MRequestRoutingBean;
import com.septemberhx.common.config.MConfig;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

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
        // todo: finish the routing algorithm. Remember to make sure that the requests from client must be satisfied
        return null;
    }
}
