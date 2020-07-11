package com.septemberhx.svcenvagent.adaptor;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.septemberhx.common.bean.agent.MInstanceRegisterNotifyRequest;
import com.septemberhx.common.bean.svcenvagent.MSvcEnvInfoBean;
import com.septemberhx.common.bean.svcenvagent.MSvcEnvInfoResponse;
import com.septemberhx.common.config.MConfig;
import com.septemberhx.svcenvagent.config.MSvcEnvConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/7/11
 *
 * Service context adaptor for eureka registry
 */
@Component
@Qualifier("svcEnvEurekaAdaptor")
public class SvcEnvEurekaAdaptor implements ISvcEnvAdaptor {

    @Qualifier("eurekaClient")
    @Autowired
    private EurekaClient discoveryClient;

    @Autowired
    private MSvcEnvConfig svcEnvConfig;

    @Override
    public MSvcEnvInfoResponse getInstanceInfoList() {
        MSvcEnvInfoResponse response = new MSvcEnvInfoResponse();
        List<MSvcEnvInfoBean> infoList = new ArrayList<>();
        for (Application application : this.discoveryClient.getApplications().getRegisteredApplications()) {
            // when the application is not supported by our framework, just jump over it.
            for (InstanceInfo info : application.getInstances()) {
                if (info.getMetadata().containsKey(MConfig.MCLUSTER_SVC_METADATA_NAME)
                        && info.getMetadata().get(MConfig.MCLUSTER_SVC_METADATA_NAME).equals(
                        MConfig.MCLUSTER_SVC_METADATA_VALUE)) {
                    // transform the object to ours
                    MSvcEnvInfoBean infoBean = new MSvcEnvInfoBean();
                    infoBean.setIp(info.getIPAddr());
                    infoBean.setPort(info.getPort());
                    infoBean.setRegistryId(info.getId());
                    infoBean.setServiceName(info.getAppName());
                    infoBean.setVersion(info.getMetadata().get(MConfig.MCLUSTER_SVC_VER_NAME));
                }
            }
        }
        response.setInfoList(infoList);
        return response;
    }

    @Override
    public void instanceRegistered(MInstanceRegisterNotifyRequest registerNotifyRequest) {
        Application app = this.discoveryClient.getApplication(this.svcEnvConfig.getNotifySvcName());
        if (app != null) {
            for (InstanceInfo info : app.getInstances()) {
                // todo: notify the target service with the instance status changes
            }
        }
    }
}
