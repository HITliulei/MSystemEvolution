package com.septemberhx.mgateway.controller;

import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.config.MConfig;
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

    private static Logger logger = LogManager.getLogger(RequestController.class);

    @PostMapping(path = MConfig.MGATEWAY_DEPENDENCY_CALL)
    @ResponseBody
    public MResponse dependencyRequest(@RequestBody MResponse requestBody, HttpServletRequest request) {
        logger.info(String.format("Receive request from %s: %s", request.getRemoteAddr(), requestBody.toString()));
        System.out.println(requestBody.toString());
        // todo: implement the function body
        return MResponse.successResponse();
    }
}
