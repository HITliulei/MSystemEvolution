package com.septemberhx.server.controller;

import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.bean.server.*;
import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.MSvcInterface;
import com.septemberhx.common.service.MSvcVersion;
import com.septemberhx.common.service.diff.MServiceDiff;
import com.septemberhx.server.algorithm.Judge.MCompatibilityJudge;
import com.septemberhx.server.client.MAnalyzerClient;
import com.septemberhx.server.client.MBuildCenterClient;
import com.septemberhx.server.model.MServerSkeleton;
import com.septemberhx.server.utils.MIDUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/1/1
 */
@RestController
@RequestMapping(value = "/service")
public class MServiceController {

    private static Logger logger = LogManager.getLogger(MServiceController.class);

    @Autowired
    private MAnalyzerClient analyzerClient;

    @Autowired
    private MBuildCenterClient mBuildCenterClient;


    @Autowired
    private RestTemplate restTemplate;

    @PostMapping(value = "/register")
    public MResponse registerService(@RequestBody MServiceRegisterBean registerBean) {
        return this.analyzerClient.analyzer(registerBean);
    }

    @PostMapping(value = "/registerOne")
    public MResponse registerOneService(@RequestBody MFetchServiceInfoBean mServiceRegisterOneBean){
        return this.analyzerClient.analyzerOne(mServiceRegisterOneBean);
    }


    @PostMapping(value = "/compare")
    public MServiceDiff compare(@RequestBody MServiceCompareNormalBean mServiceCompareNormalBean){
        MServiceCompareBean mServiceCompareBean = new MServiceCompareBean();
        MService mService1 = MServerSkeleton.getCurrSvcManager().getByServiceNameAndVersion(
                mServiceCompareNormalBean.getServiceName(),
                MSvcVersion.fromStr(mServiceCompareNormalBean.getFixedService()).toString()
        ).get();
        MService mService2 = MServerSkeleton.getCurrSvcManager().getByServiceNameAndVersion(
                mServiceCompareNormalBean.getServiceName(),
                MSvcVersion.fromStr(mServiceCompareNormalBean.getComparedService()).toString()
        ).get();
        if(MCompatibilityJudge.ifHightVersion(mService1.getServiceVersion().toString(),mService2.getServiceVersion().toString())){
            mServiceCompareBean.setFixedService(mService1);
            mServiceCompareBean.setComparedService(mService2);
        }else{
            mServiceCompareBean.setFixedService(mService2);
            mServiceCompareBean.setComparedService(mService1);
        }
        return analyzerClient.compare(mServiceCompareBean);
    }


    @ResponseBody
    @RequestMapping(value = "/pushServiceInfos",method = RequestMethod.POST)
    public MResponse pushServiceInfos(@RequestBody MServiceAnalyzeResultBean resultBean) {
        logger.info("fanhuixinxi");
        // step 1, fetch the service list
        List<MService> serviceList = resultBean.getServiceList();
        if (serviceList.isEmpty()) {
            return MResponse.successResponse();
        }

        for (MService service : serviceList) {
            logger.info("正在保存服务" + service.toString());
            // step 2, save all the services to the model and database
            service.setId(service.getServiceName()+"_"+service.getServiceVersion().toString());
            for (MSvcInterface serviceInterface : service.getServiceInterfaceMap().values()) {
                serviceInterface.setId(MIDUtils.uniqueInterfaceId(service.getServiceName(), serviceInterface.getFunctionName()));
                serviceInterface.setServiceId(service.getId());
            }
            MServerSkeleton.getCurrSvcManager().registerService(service);
            // step 3, if the image url doesn't exist, build it. and update into database.
            String git = service.getGitUrl();
            MBuildJobBean mBuildJobBean = new MBuildJobBean();
            mBuildJobBean.setGitUrl(git);
            mBuildJobBean.setGitTag(service.getServiceVersion().toCommonStr());
            mBuildJobBean.setServiceName(service.getServiceName());
            mBuildJobBean.setId(service.getServiceName()+"_"+service.getServiceVersion().toString());
            mBuildCenterClient.build(mBuildJobBean);
        }
        return MResponse.successResponse();
    }
}
