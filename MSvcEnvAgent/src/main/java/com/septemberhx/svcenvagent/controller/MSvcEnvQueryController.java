package com.septemberhx.svcenvagent.controller;

import com.septemberhx.common.bean.svcenvagent.MSvcEnvInfoResponse;
import com.septemberhx.svcenvagent.adaptor.ISvcEnvAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/7/11
 */
@RestController
public class MSvcEnvQueryController {

    @Qualifier("svcEnvEurekaAdaptor")
    @Autowired
    private ISvcEnvAdaptor svcEnvAdaptor;

    @ResponseBody
    @RequestMapping(path = "/instanceInfoList", method = RequestMethod.GET)
    public MSvcEnvInfoResponse getInstanceInfoList() {
        return this.svcEnvAdaptor.getInstanceInfoList();
    }
}
