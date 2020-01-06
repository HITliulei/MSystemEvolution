package com.septemberhx.server.controller;

import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.bean.server.MBuildJobBean;
import com.septemberhx.common.bean.server.MBuildJobFinishedBean;
import com.septemberhx.common.bean.server.MServiceRegisterBean;
import com.septemberhx.common.service.MService;
import com.septemberhx.server.client.MAnalyzerClient;
import com.septemberhx.server.job.MBaseJob;
import com.septemberhx.server.job.MBuildJob;
import com.septemberhx.server.job.MJobExecutor;
import com.septemberhx.server.job.MJobType;
import com.septemberhx.server.model.MServerSkeleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

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
    public void registerService(@RequestBody MServiceRegisterBean registerBean) {
        // step 1, fetch all the information of each version
        List<MService> serviceList = this.analyzerClient.analyzer(registerBean);

        // step 2, if the image url doesn't exist, build it.
        for (MService service : serviceList) {
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
                .getServicesByServiceName(registerBean.getServiceName());

        // step 4, save all the services to the model and database
        for (MService service : serviceList) {
            MServerSkeleton.getCurrSvcManager().registerService(service);
        }
    }

    @PostMapping(value = "/buildNotify")
    public MResponse buildNotify(@RequestBody MBuildJobFinishedBean finishedBean) {
        // update the image url in the database
        if (finishedBean.isSuccess()) {
            Optional<MBaseJob> jobOptional = MServerSkeleton.getCurrJobManager().getById(finishedBean.getId());
            if (jobOptional.isPresent() && jobOptional.get().getType() == MJobType.JOB_BUILD) {
                MBuildJob buildJob = (MBuildJob) jobOptional.get();
                MServerSkeleton.getCurrSvcManager().updateImageUrl(buildJob.getServiceId(), finishedBean.getImageUrl());
                buildJob.markAsDone();
            }
        }

        return MResponse.successResponse();
    }
}
