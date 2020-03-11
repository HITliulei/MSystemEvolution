package com.septemberhx.mgateway.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.config.MConfig;
import com.septemberhx.common.config.Mvf4msDep;
import com.septemberhx.common.service.dependency.BaseSvcDependency;
import com.septemberhx.mgateway.core.MGatewayInfo;
import com.septemberhx.mgateway.core.MGatewayRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/2
 */
@Controller
public class RequestController {

    @Autowired
    private MGatewayRequest gatewayRequest;

    private static final Logger logger = LogManager.getLogger(RequestController.class);
    private Gson gson = new GsonBuilder().create();

    @PostMapping(path = MConfig.MGATEWAY_DEPENDENCY_CALL)
    @ResponseBody
    public MResponse dependencyRequest(@RequestBody MResponse requestBody, HttpServletRequest request) {
        logger.info(String.format("Receive request from %s: %s", request.getRemoteAddr(), requestBody.toString()));
        Mvf4msDep dep = gson.fromJson(gson.toJson(requestBody.get(MConfig.MGATEWAY_DEPENDENCY_ID)), Mvf4msDep.class);
        BaseSvcDependency baseSvcDependency = BaseSvcDependency.tranConfig2Dependency(dep);

        String userId = (String) requestBody.get(MConfig.MGATEWAY_CLIENT_ID);
        if (userId != null) {
            MGatewayInfo.inst().addRequestInQueue(userId, baseSvcDependency, requestBody);
            return MResponse.successResponse();
        } else {
            return this.gatewayRequest.solveInstDepRequest(request.getRemoteAddr(), baseSvcDependency, requestBody);
        }
    }
}
