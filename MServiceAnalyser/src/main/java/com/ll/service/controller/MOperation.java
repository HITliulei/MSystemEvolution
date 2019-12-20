package com.ll.service.controller;

import com.ll.service.bean.MPathInfo;
import com.ll.service.utils.GetServiceDiff;
import com.ll.service.utils.GetServiceInfo;
import com.ll.service.utils.GetSourceCode;
import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.MServiceDiff;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

/**
 * Created by Lei on 2019/12/16 21:40
 */

@RestController
public class MOperation {

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
    @RequestMapping("getDiifTwoVersions")
    public MServiceDiff getServiceDiff(@RequestParam("version1")String version1,@RequestParam("version2")String version2){
        if(this.url == null){
            return null;
        }else{
            return GetServiceDiff.getDiff(GetServiceInfo.getMservice(version1,this.map.get(version1)),GetServiceInfo.getMservice(version2,this.map.get(version2)));
        }
    }
}
