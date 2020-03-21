package com.septemberhx.server.controller;

import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.bean.server.MBuildJobFinishedBean;
import com.septemberhx.common.bean.server.MJobFinishedBean;
import com.septemberhx.server.job.MBaseJob;
import com.septemberhx.server.job.MBuildJob;
import com.septemberhx.server.job.MJobType;
import com.septemberhx.server.model.MServerSkeleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/1/10
 */
@RestController
@RequestMapping(value = "/job")
public class MJobController {

    private static Logger logger = LogManager.getLogger(MJobController.class);

    @PostMapping(value = "/buildNotify")
    @ResponseBody
    public MResponse buildNotify(@RequestBody MBuildJobFinishedBean finishedBean) {
        logger.info(String.format("BuildNotify: %s", finishedBean.toString()));
        
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

    @PostMapping(value = "/notify")
    @ResponseBody
    public MResponse jobNotify(@RequestBody MJobFinishedBean finishedBean) {
        if (MServerSkeleton.getInstance().getExecutor() != null) {
            MServerSkeleton.getInstance().getExecutor().jobFinished(finishedBean.getJobId());
        }
        return MResponse.successResponse();
    }
}
