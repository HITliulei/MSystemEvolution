package com.septemberhx.mclient.controller;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.discovery.EurekaClient;
import com.septemberhx.common.config.MConfig;
import com.septemberhx.common.bean.mclient.MApiContinueRequest;
import com.septemberhx.common.bean.mclient.MApiSplitBean;
import com.septemberhx.common.bean.mclient.MClientInfoBean;
import com.septemberhx.common.bean.mclient.MInstanceRestInfoBean;
import com.septemberhx.mclient.config.Mvf4msConfig;
import com.septemberhx.mclient.core.MClientSkeleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
        return MClientSkeleton.inst().getMObjectIdList();
    }

    @Qualifier("eurekaClient")
    @Autowired
    private EurekaClient discoveryClient;

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @Autowired
    private Mvf4msConfig mvf4msConfig;

    private static Logger logger = LogManager.getLogger(MClientController.class);

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
        map.put(MConfig.MCLUSTER_SVC_VER_NAME, this.mvf4msConfig.getVersion());
        MClientSkeleton.inst().setDiscoveryClient(this.discoveryClient);
        MClientSkeleton.inst().setRequestMappingHandlerMapping(this.handlerMapping);
        MClientSkeleton.inst().setMvf4msConfig(this.mvf4msConfig);
        logger.info(this.mvf4msConfig.toString());
    }

    @ResponseBody
    @RequestMapping(path = "/info", method = RequestMethod.GET)
    public MClientInfoBean getInfo() {
        MClientInfoBean infoBean = new MClientInfoBean();
        infoBean.setApiMap(MClientSkeleton.inst().getObjectId2ApiSet());
        infoBean.setParentIdMap(MClientSkeleton.inst().getParentIdMap());
        infoBean.setMObjectIdSet(new HashSet<>(MClientSkeleton.inst().getMObjectIdList()));
        return infoBean;
    }

    @RequestMapping(path = "/setRestInfo", method = RequestMethod.POST)
    public void setRestInfo(@RequestBody MInstanceRestInfoBean restInfoBean) {
        MClientSkeleton.inst().addRestInfo(restInfoBean);
    }

    @RequestMapping(path = "/setApiContinueStatus", method = RequestMethod.POST)
    public void setApiContinueStatus(@RequestBody MApiContinueRequest continueStatus) {
        for (MApiSplitBean splitBean : continueStatus.getSplitBeans()) {
            MClientSkeleton.inst().setApiContinueStatus(splitBean);
        }
    }

    @ResponseBody
    @RequestMapping(path = "/getRestInfoList", method = RequestMethod.GET)
    public List<MInstanceRestInfoBean> getRestInfoList() {
        return MClientSkeleton.inst().getRestInfoBeanList();
    }
}
