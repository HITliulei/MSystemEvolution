package com.septemberhx.server.controller;

import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.bean.server.MServiceAnalyzeResultBean;
import com.septemberhx.common.bean.server.MServiceRegisterBean;
import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.MServiceInterface;
import com.septemberhx.server.client.MAnalyzerClient;
import com.septemberhx.server.job.MBuildJob;
import com.septemberhx.server.job.MJobExecutor;
import com.septemberhx.server.model.MServerSkeleton;
import com.septemberhx.server.utils.MIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping(value = "/register")
    public MResponse registerService(@RequestBody MServiceRegisterBean registerBean) {
        return this.analyzerClient.analyzer(registerBean);
    }

    @PostMapping(value = "/pushServiceInfos")
    public MResponse pushServiceInfos(@RequestBody MServiceAnalyzeResultBean resultBean) {
        // step 1, fetch the service list
        List<MService> serviceList = resultBean.getServiceList();
        if (serviceList.isEmpty()) {
            return MResponse.successResponse();
        }

        // step 2, if the image url doesn't exist, build it.
        for (MService service : serviceList) {
            service.setId(service.getServiceName());
            for (MServiceInterface serviceInterface : service.getServiceInterfaceMap().values()) {
                serviceInterface.setId(MIDUtils.uniqueInterfaceId(service.getServiceName(), serviceInterface.getFunctionName()));
            }

            // construct the build job and execute it
            MBuildJob buildJob = new MBuildJob(
                    service.getServiceName(),
                    service.getGitUrl(),
                    service.getServiceVersion().toCommonStr(),
                    service.getId()
            );
            MServerSkeleton.getCurrJobManager().update(buildJob);
            MJobExecutor.start(buildJob);
        }

        // todo: step 3, join compare the services, and store the differences
        List<MService> existServiceList = MServerSkeleton.getCurrSvcManager()
                .getServicesByServiceName(serviceList.get(0).getServiceName());

        // step 4, save all the services to the model and database
        for (MService service : serviceList) {
            MServerSkeleton.getCurrSvcManager().registerService(service);
        }

        return MResponse.successResponse();
    }
}
