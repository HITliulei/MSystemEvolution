package com.septemberhx.mclient.controller;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.discovery.EurekaClient;
import com.septemberhx.common.config.MConfig;
import com.septemberhx.common.bean.mclient.MApiContinueRequest;
import com.septemberhx.common.bean.mclient.MApiSplitBean;
import com.septemberhx.common.bean.mclient.MClientInfoBean;
import com.septemberhx.common.bean.mclient.MInstanceRestInfoBean;
import com.septemberhx.mclient.core.MClientSkeleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @Author: septemberhx
 * @Date: 2019-06-13
 * @Version 0.1
 */
@RestController
@EnableAutoConfiguration
@RequestMapping("/mclient")
public class MClientController {

    @Qualifier("eurekaApplicationInfoManager")
    @Autowired
    private ApplicationInfoManager aim;

    @ResponseBody
    @RequestMapping(path = "/getMObjectIdList", method = RequestMethod.GET)
    public List<String> getMObjectIdList() {
        return MClientSkeleton.getInstance().getMObjectIdList();
    }

    @Qualifier("eurekaClient")
    @Autowired
    private EurekaClient discoveryClient;

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @Value("${mvf4ms.version}")
    private String serviceVersion;

    /**
     * Do something for MClient App:
     *   * Register new metadata so we can identify whether it is a MClient app or not
     *
     *   2020/02/29
     *   * Register version info that extracted from application.[yaml|properties]
     */
    @PostConstruct
    public void init() {
        Map<String, String> map = aim.getInfo().getMetadata();
        map.put(MConfig.MCLUSTER_SVC_METADATA_NAME, MConfig.MCLUSTER_SVC_METADATA_VALUE);
        map.put(MConfig.MCLUSTER_SVC_VER_NAME, this.serviceVersion);
        MClientSkeleton.getInstance().setDiscoveryClient(this.discoveryClient);
        MClientSkeleton.getInstance().setRequestMappingHandlerMapping(this.handlerMapping);
    }

    @ResponseBody
    @RequestMapping(path = "/info", method = RequestMethod.GET)
    public MClientInfoBean getInfo() {
        MClientInfoBean infoBean = new MClientInfoBean();
        infoBean.setApiMap(MClientSkeleton.getInstance().getObjectId2ApiSet());
        infoBean.setParentIdMap(MClientSkeleton.getInstance().getParentIdMap());
        infoBean.setMObjectIdSet(new HashSet<>(MClientSkeleton.getInstance().getMObjectIdList()));
        return infoBean;
    }

    @RequestMapping(path = "/setRestInfo", method = RequestMethod.POST)
    public void setRestInfo(@RequestBody MInstanceRestInfoBean restInfoBean) {
        MClientSkeleton.getInstance().addRestInfo(restInfoBean);
    }

    @RequestMapping(path = "/setApiContinueStatus", method = RequestMethod.POST)
    public void setApiContinueStatus(@RequestBody MApiContinueRequest continueStatus) {
        for (MApiSplitBean splitBean : continueStatus.getSplitBeans()) {
            MClientSkeleton.getInstance().setApiContinueStatus(splitBean);
        }
    }

    @ResponseBody
    @RequestMapping(path = "/getRestInfoList", method = RequestMethod.GET)
    public List<MInstanceRestInfoBean> getRestInfoList() {
        return MClientSkeleton.getInstance().getRestInfoBeanList();
    }
}
