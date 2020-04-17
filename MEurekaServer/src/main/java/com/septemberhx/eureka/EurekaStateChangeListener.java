package com.septemberhx.eureka;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.septemberhx.common.bean.agent.MInstanceRegisterNotifyRequest;
import com.septemberhx.common.config.MConfig;
import com.septemberhx.common.utils.MRequestUtils;
import com.septemberhx.common.utils.MUrlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceCanceledEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRegisteredEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRenewedEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaRegistryAvailableEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaServerStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

@Component
public class EurekaStateChangeListener {

    private static Logger logger = LogManager.getLogger(EurekaStateChangeListener.class);

    @Qualifier("eurekaClient")
    @Autowired
    EurekaClient eurekaClient;

    @EventListener
    public void listen(EurekaInstanceCanceledEvent eurekaInstanceCanceledEvent) {
        //服务断线事件
        String appName = eurekaInstanceCanceledEvent.getAppName();
        String serverId = eurekaInstanceCanceledEvent.getServerId();
    }

    @EventListener
    public void listen(EurekaInstanceRegisteredEvent event) {
        Application clusterAgentApp = this.eurekaClient.getApplication(MConfig.MCLUSTERAGENT_NAME);
        System.out.println("=====");
        for (Application app : this.eurekaClient.getApplications().getRegisteredApplications()) {
            System.out.println(app.getName());
        }
        System.out.println("-----");
        if (clusterAgentApp != null) {
            InstanceInfo clusterInfo = clusterAgentApp.getInstances().get(0);
            InstanceInfo instanceInfo = event.getInstanceInfo();
            System.out.println(instanceInfo.getAppName() + "|" + instanceInfo.getInstanceId() + "|" + instanceInfo.getIPAddr() + "|" + instanceInfo.getPort());
            if (!instanceInfo.getAppName().equalsIgnoreCase(MConfig.MCLUSTERAGENT_NAME) && instanceInfo.getPort() != 0) {
                try {
                    MInstanceRegisterNotifyRequest notifyRequest = new MInstanceRegisterNotifyRequest();
                    notifyRequest.setIp(instanceInfo.getIPAddr());
                    notifyRequest.setPort(instanceInfo.getPort());
                    notifyRequest.setInstanceInfo(instanceInfo);
                    MRequestUtils.sendRequest(
                            MUrlUtils.getRemoteUri(
                                    clusterInfo.getIPAddr(),
                                    clusterInfo.getPort(),
                                    MConfig.MCLUSTERAGENT_INSTANCE_REGISTER_URL
                            ),
                            notifyRequest,
                            null,
                            RequestMethod.POST
                    );
                    logger.info(instanceInfo);
                } catch (Exception e) {
                    logger.info(e);
                    logger.warn("Failed to connect to MClusterAgent!");
                }
            }
        }
    }

    @EventListener
    public void listen(EurekaInstanceRenewedEvent event) {
        event.getAppName();
        event.getServerId();
    }

    @EventListener
    public void listen(EurekaRegistryAvailableEvent event) {

    }

    @EventListener
    public void listen(EurekaServerStartedEvent event) {
        //Server启动
    }
}