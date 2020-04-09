package com.septemberhx.server.controller;

import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.bean.server.*;
import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.MSvcInterface;
import com.septemberhx.server.client.MAnalyzerClient;
import com.septemberhx.server.client.MBuildCenterClient;
import com.septemberhx.server.job.MBuildJob;
import com.septemberhx.server.job.MJobExecutor;
import com.septemberhx.server.model.MServerSkeleton;
import com.septemberhx.server.utils.MIDUtils;
import com.septemberhx.server.utils.MServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private MBuildCenterClient mBuildCenterClient;

    @PostMapping(value = "/register")
    @ResponseBody
    public MResponse registerService(@RequestBody MServiceRegisterBean registerBean) {
        return this.analyzerClient.analyzer(registerBean);
    }

    @PostMapping(value = "/registerOne")
    @ResponseBody
    public MResponse registerOneService(@RequestBody MFetchServiceInfoBean mServiceRegisterOneBean){
        return this.analyzerClient.analyzerOne(mServiceRegisterOneBean);
    }

    @PostMapping(value = "/allServices")
    @ResponseBody
    public List<MService> getAllServices() {
        return MServerSkeleton.getCurrSvcManager().getAllValues();
    }

    @PostMapping(value = "/compare")
    @ResponseBody
    public MResponse compare(@RequestBody MServiceCompareNormalBean mServiceCompareNormalBean){
        return null;
    }

    @PostMapping(value = "/pushServiceInfos")
    @ResponseBody
    public MResponse pushServiceInfos(@RequestBody MServiceAnalyzeResultBean resultBean) {
        // step 1, fetch the service list
        List<MService> serviceList = resultBean.getServiceList();
        if (serviceList.isEmpty()) {
            return MResponse.successResponse();
        }


        for (MService service : serviceList) {
            // step 2, save all the services to the model and database
            service.setId(service.getServiceName()+"_"+service.getServiceVersion().toCommonStr());
            for (MSvcInterface serviceInterface : service.getServiceInterfaceMap().values()) {
                serviceInterface.setId(MIDUtils.uniqueInterfaceId(service.getServiceName(), serviceInterface.getFunctionName()));
                serviceInterface.setServiceId(service.getId());
            }
            MServerSkeleton.getCurrSvcManager().registerService(service);

//            // step 3, if the image url doesn't exist, build it. and update into database.
//            String git = service.getGitUrl();
//            MBuildJobBean mBuildJobBean = new MBuildJobBean();
//            mBuildJobBean.setGitUrl(git);
//            mBuildJobBean.setGitTag(service.getServiceVersion().toCommonStr());
//            mBuildJobBean.setServiceName(service.getServiceName());
//            mBuildJobBean.setId(service.getServiceName()+"_"+service.getServiceVersion().toString());
//            MServiceUtils.doBuildJob(mBuildJobBean);
        }

        // step 2, if the image url doesn't exist, build it.
        for (MService service : serviceList) {
            service.setId(service.getServiceName());
            for (MSvcInterface serviceInterface : service.getServiceInterfaceMap().values()) {
                serviceInterface.setId(MIDUtils.uniqueInterfaceId(service.getServiceName(), serviceInterface.getFunctionName()));
                serviceInterface.setServiceId(service.getId());
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

        return MResponse.successResponse();
    }
}
