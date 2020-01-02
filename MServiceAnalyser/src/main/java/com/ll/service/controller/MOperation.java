package com.ll.service.controller;

import com.ll.service.bean.MPathInfo;
import com.ll.service.utils.GetServiceDiff;
import com.ll.service.utils.GetServiceInfo;
import com.ll.service.utils.GetSourceCode;
import com.septemberhx.common.bean.server.MFetchServiceInfoBean;
import com.septemberhx.common.bean.server.MServiceCompareBean;
import com.septemberhx.common.bean.server.MServiceRegisterBean;
import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.MServiceDiff;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by Lei on 2019/12/16 21:40
 */

@RestController
public class MOperation {

    private static Logger logger = LogManager.getLogger(MOperation.class);

    @RequestMapping("getAllVersion")
    public List<MService> getAllVersionInfo(@RequestBody MServiceRegisterBean mServiceRegisterBean){
        Map<String, MPathInfo> map = GetSourceCode.getCodeAndGetMPathInfo(mServiceRegisterBean.getGitUrl());
        List<MService> list = new ArrayList<>();
        for(String version:map.keySet()){
            list.add(GetServiceInfo.getMservice(version,map.get(version)));
            logger.info("版本:" + version + " 信息得到 ");
        }
        return list;
    }

    @RequestMapping("getVersionInfo")
    public Callable<MService> getVersionInfo1(@RequestBody MFetchServiceInfoBean mFetchServiceInfoBean){
        Callable<MService> result = new Callable<MService>(){
            public MService call() throws Exception {
                logger.info("异步处理得到源码信息");
                String callback = mFetchServiceInfoBean.getCallBackUrl();
                String version = "";
                version = version + mFetchServiceInfoBean.getVersion().getMainVersionNum()+mFetchServiceInfoBean.getVersion().getChildVersionNum()+mFetchServiceInfoBean.getVersion().getFixVersionNum();
                MPathInfo mPathInfo = GetSourceCode.getCodeByVersion(mFetchServiceInfoBean.getGitUrl(),"v"+version);
                MService mService = GetServiceInfo.getMservice(version,mPathInfo);
                logger.info("将结果返回到callback中");
                new RestTemplate().postForLocation(callback,mService);
                return mService;
            }
        } ;
        return result;
    }

    @RequestMapping("getDiffBetweenTwoVersions")
    public MServiceDiff getServiceDiff(@RequestBody MServiceCompareBean mServiceCompareBean){
        logger.info("分析版本间的差异");
        return  GetServiceDiff.getDiff(mServiceCompareBean.getFixedService(),mServiceCompareBean.getComparedService());
    }

    @RequestMapping("GetDiffTwoVersions1")
    public MServiceDiff getServiceDiff1(@RequestBody MService mService1 , @RequestBody MService mService2){
        logger.info("分析版本间的差异");
        return GetServiceDiff.getDiff(mService1,mService2);
    }
}
