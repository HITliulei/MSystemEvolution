package com.ll.service.controller;

import com.ll.service.bean.MPathInfo;
import com.ll.service.client.MServerClient;
import com.ll.service.utils.GetServiceDiff;
import com.ll.service.utils.GetServiceInfo;
import com.ll.service.utils.GetSourceCode;
import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.bean.server.MFetchServiceInfoBean;
import com.septemberhx.common.bean.server.MServiceAnalyzeResultBean;
import com.septemberhx.common.bean.server.MServiceCompareBean;
import com.septemberhx.common.bean.server.MServiceRegisterBean;
import com.septemberhx.common.config.MConfig;
import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.diff.MServiceDiff;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Lei on 2019/12/16 21:40
 */
@RestController
public class MOperation {
    @Autowired
    private MServerClient serverClient;

    private static Logger logger = LogManager.getLogger(MOperation.class);
    private static ExecutorService executorService = Executors.newFixedThreadPool(8);

    @PostMapping(MConfig.ANALYZE_ANALYZE_URI)
    public MResponse getAllVersionInfo(@RequestBody MServiceRegisterBean mServiceRegisterBean) {
        executorService.submit(() -> {
            Map<String, MPathInfo> map = GetSourceCode.getCodeAndGetMPathInfo(mServiceRegisterBean.getGitUrl());
            List<MService> serviceList = new ArrayList<>();
            for (Map.Entry<String, MPathInfo> entry : map.entrySet()) {
                MService service = GetServiceInfo.getMservice(entry.getKey(), entry.getValue());
                service.setServiceName(mServiceRegisterBean.getServiceName());
                logger.info(service.toString());
                serviceList.add(service);
            }

            logger.info("Trying to send service infos to server...");
            MServiceAnalyzeResultBean mServiceAnalyzeResultBean = new MServiceAnalyzeResultBean();
            mServiceAnalyzeResultBean.setServiceList(serviceList);
            MResponse mResponse = this.serverClient.pushServiceInfos(mServiceAnalyzeResultBean);
            logger.info(String.format("Receive %s from server", mResponse.getStatus()));
        });
        return MResponse.successResponse();
    }

    @PostMapping(MConfig.ANALYZE_ANALYZE_URI_ONE)
    public MResponse getVersionInfo1(@RequestBody MFetchServiceInfoBean mFetchServiceInfoBean) {
        executorService.submit(() -> {
            logger.info ("异步处理得到源码信息");
            String version = mFetchServiceInfoBean.getVersion().toString();
            MPathInfo mPathInfo = GetSourceCode.getCodeByVersion(mFetchServiceInfoBean.getGitUrl(), "v" + version);
            MService mService = GetServiceInfo.getMservice(version, mPathInfo);
            logger.info ("将结果返回到callback中");
            List<MService> list = new ArrayList<>();
            list.add(mService);
            MServiceAnalyzeResultBean mServiceAnalyzeResultBean = new MServiceAnalyzeResultBean();
            mServiceAnalyzeResultBean.setServiceList(list);
            MResponse mResponse = this.serverClient.pushServiceInfos(mServiceAnalyzeResultBean);
            logger.info(String.format("Receive %s from server", mResponse.getStatus()));
        });
        return MResponse.successResponse();
    }

    @PostMapping(MConfig.ANALYZE_COMPARE_URI)
    public MServiceDiff getServiceDiff(@RequestBody MServiceCompareBean mServiceCompareBean) {
        logger.info ("分析版本间的差异");
        return GetServiceDiff.getDiff(mServiceCompareBean.getFixedService(), mServiceCompareBean.getComparedService());
    }

    @PostMapping("GetDiffTwoVersions1")
    public MServiceDiff getServiceDiff1(@RequestBody MService mService1, @RequestBody MService mService2) {
        logger.info ("分析版本间的差异");
        return GetServiceDiff.getDiff(mService1, mService2);
    }

    @GetMapping("test")
    public MService test(@RequestParam("git")String gitUrl, @RequestParam("version")String version) {
        MPathInfo mPathInfo = GetSourceCode.getCodeByVersion(gitUrl, "v" + version);
        MService mService = GetServiceInfo.getMservice(version, mPathInfo);
        return mService;
    }


}
