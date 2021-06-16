package com.septemberhx.server.controller;

import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.bean.server.MBuildJobFinishedBean;
import com.septemberhx.common.service.MSvcVersion;
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
    public MResponse buildNotify(@RequestBody MBuildJobFinishedBean finishedBean) {
        logger.info(String.format("BuildNotify: %s", finishedBean.toString()));

        // update the image url in the database
        if (finishedBean.isSuccess()) {
            String serviceId = finishedBean.getId().split("_")[0] + "_" + MSvcVersion.fromStr(finishedBean.getId().split("_")[1]).toString();
            MServerSkeleton.getCurrSvcManager().updateImageUrl(serviceId, finishedBean.getImageUrl());
        }

        return MResponse.successResponse();
    }
}
