package com.septemberhx.mgateway.controller;

import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.config.MConfig;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/2
 */
@Controller
public class RequestController {

    @PostMapping(path = MConfig.MGATEWAY_DEPENDENCY_CALL)
    @ResponseBody
    public MResponse dependencyRequest(@RequestBody MResponse requestBody) {
        System.out.println(requestBody.toString());
        // todo: implement the function body
        return MResponse.successResponse();
    }
}
