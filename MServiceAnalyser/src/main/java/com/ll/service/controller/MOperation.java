package com.ll.service.controller;

import com.ll.service.bean.MPathInfo;
import com.ll.service.utils.GetServiceDiff;
import com.ll.service.utils.GetServiceInfo;
import com.ll.service.utils.GetSourceCode;
import com.septemberhx.common.bean.server.MFetchServiceInfoBean;
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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by Lei on 2019/12/16 21:40
 */

@RestController
public class MOperation {

    private static Logger logger = LogManager.getLogger(MOperation.class);

    private String url;

    private Map<String, MPathInfo> map;

    @RequestMapping("getAllversion")
    public Set<String> getAllversionInfo(@RequestParam("url")String url){
        Map<String, MPathInfo> map = GetSourceCode.getCodeAndGetMPathInfo(url);
        this.map = map;
        this.url = url;
        return map.keySet();
    }

    @RequestMapping("getVersionInfo")
    public MService getVersionInfo(@RequestParam("version")String version){
        if(this.url == null){
            return null;
        }else{
            return GetServiceInfo.getMservice(version,this.map.get(version));
        }
    }
    @RequestMapping("getVersionInfo1")
    public Callable<MService> getVersionInfo1(@RequestBody MFetchServiceInfoBean mFetchServiceInfoBean){
        Callable<MService> result = new Callable<MService>(){
            public MService call() throws Exception {
                String callback = mFetchServiceInfoBean.getCallBackUrl();
                String version = "";
                version = version + mFetchServiceInfoBean.getVersion().getMainVersionNum()+mFetchServiceInfoBean.getVersion().getChildVersionNum()+mFetchServiceInfoBean.getVersion().getFixVersionNum();
                MPathInfo mPathInfo = GetSourceCode.getCodeByVersion(mFetchServiceInfoBean.getGitUrl(),"v"+version);
                MService mService = GetServiceInfo.getMservice(version,mPathInfo);
                new RestTemplate().postForLocation(callback,mService);
                return mService;
            }
        } ;
        return result;
    }
    @RequestMapping("getDiifTwoVersions")
    public MServiceDiff getServiceDiff(@RequestParam("version1")String version1,@RequestParam("version2")String version2){
        if(this.url == null){
            return null;
        }else{
            return GetServiceDiff.getDiff(GetServiceInfo.getMservice(version1,this.map.get(version1)),GetServiceInfo.getMservice(version2,this.map.get(version2)));
        }
    }
    @RequestMapping("GetDiffTwoVersions1")
    public MServiceDiff getServiceDiff1(@RequestBody MService mService1 , @RequestBody MService mService2){
        return GetServiceDiff.getDiff(mService1,mService2);
    }
}
