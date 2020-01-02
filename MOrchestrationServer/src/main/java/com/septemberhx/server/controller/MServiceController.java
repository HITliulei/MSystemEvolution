package com.septemberhx.server.controller;

import com.septemberhx.common.bean.server.MServiceRegisterBean;
import com.septemberhx.common.service.MService;
import com.septemberhx.server.client.MAnalyzerClient;
import com.septemberhx.server.model.MServerSkeleton;
import com.septemberhx.server.model.MServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/1/1
 */
@RestController
@RequestMapping(value = "/service")
public class MServiceController {

    @Autowired
    private MAnalyzerClient analyzerClient;

    @RequestMapping(value = "/register")
    public void registerService(@RequestBody MServiceRegisterBean registerBean) {
        // step 1, fetch all the information of each version
        List<MService> serviceList = this.analyzerClient.analyzer(registerBean);

        // step 2, if the image url doesn't exist, build it.
        for (MService service : serviceList) {
            // todo: check the existence of the image url, and build if not exists
        }

        // todo: step 3, join compare the services, and store the differences
        List<MService> existServiceList = MServerSkeleton.getInstance()
                .getCurrSystemModel().getServiceManager().getServicesByServiceName(registerBean.getServiceName());
    }
}
