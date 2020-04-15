package com.septemberhx.server.model;

import com.septemberhx.common.base.node.MServerNode;
import com.septemberhx.common.bean.agent.MInstanceInfoBean;
import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.MSvcInstance;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/1/1
 */
public class MServerSkeleton {

    @Getter
    @Setter
    private MSystemModel currSystemModel;       // record current info about the system

    @Getter
    @Setter
    private MSystemModel nextSystemModel;       // record the system info after evolution

    @Getter
    @Setter
    private MDeployExecutorInterface executor;

    private static MServerSkeleton instance;
    private static Logger logger = LogManager.getLogger(MServerSkeleton.class);

    private MServerSkeleton() {
        this.currSystemModel = new MSystemModel();
        this.nextSystemModel = new MSystemModel();
    }

    public static MServerSkeleton getInstance() {
        if (instance == null) {
            synchronized (MServerSkeleton.class) {
                instance = new MServerSkeleton();
            }
        }
        return instance;
    }

    public void syncInstanceInfo(MInstanceInfoBean instanceInfo) {
        String nodeId = null;
        if (instanceInfo.getDockerInfo() != null) {
            if (instanceInfo.getDockerInfo().getHostIp() == null) {
                instanceInfo.getDockerInfo().setHostIp("60.205.188.102");
            }
            MServerNode node = MServerSkeleton.getCurrNodeManager().getByIp(
                    instanceInfo.getClusterId(), instanceInfo.getDockerInfo().getHostIp());
            if (node != null) {
                nodeId = node.getId();
            }

            // check if the instance is alive. The mObjectIdMap will not be null if alive
            String containerInstanceId = instanceInfo.getDockerInfo().getInstanceId();
            if (instanceInfo.getMObjectIdMap() != null) {
                MSvcInstance instance = new MSvcInstance(
                        instanceInfo.getParentIdMap(),
                        instanceInfo.getClusterId(),
                        nodeId,
                        instanceInfo.getIp(),
                        instanceInfo.getPort(),
                        containerInstanceId,
                        instanceInfo.getMObjectIdMap(),
                        "",
                        "",
                        instanceInfo.getRegistryId(),
                        instanceInfo.getVersion()
                );

                // get actual serviceId of the service instance
                Optional<MSvcInstance> instanceOptional = MServerSkeleton.getNextInstManager().getById(containerInstanceId);
                if (instanceOptional.isPresent()) {
                    MSvcInstance currInstance = instanceOptional.get();
                    instance.setServiceId(currInstance.getServiceId());
                    instance.setServiceName(currInstance.getServiceName());
                } else {  // this means the instance was created in before running. We have to get the real serviceId of it
                    Optional<MService> serviceOptional = MServerSkeleton.getCurrSvcManager().getByServiceNameAndVersion(
                            instanceInfo.getServiceName(), instanceInfo.getVersion());
                    if (serviceOptional.isPresent()) {
                        instance.setServiceName(serviceOptional.get().getServiceName());
                        instance.setServiceId(serviceOptional.get().getId());
                    } else {
                        return;  // if we can't recognise the service, then ignore it
                    }
                }

                MServerSkeleton.getCurrInstManager().update(instance);
            }
        } else {
            Optional<MSvcInstance> instanceOptional = MServerSkeleton.getCurrInstManager().getByClusterIdAndRegistryId(
                    instanceInfo.getClusterId(), instanceInfo.getRegistryId()
            );
            if (instanceOptional.isPresent()){
                // remove the useless info when the instance is dead
                String containerInstanceId = instanceOptional.get().getId();
                MServerSkeleton.getCurrInstManager().remove(containerInstanceId);
                logger.info(String.format("Instance %s is deleted due to empty docker info", containerInstanceId));
            }
        }
    }

    public static MSvcManager getCurrSvcManager() {
        return MServerSkeleton.getInstance().getCurrSystemModel().getServiceManager();
    }

    public static MSvcManager getNextSvcManager() {
        return MServerSkeleton.getInstance().getNextSystemModel().getServiceManager();
    }

    public static MJobManager getCurrJobManager() {
        return MServerSkeleton.getInstance().getCurrSystemModel().getJobManager();
    }

    public static MClusterManager getCurrNodeManager() {
        return MServerSkeleton.getInstance().getCurrSystemModel().getNodeManager();
    }

    public static MSvcInstManager getCurrInstManager() {
        return MServerSkeleton.getInstance().getCurrSystemModel().getInstanceManager();
    }

    public static MSvcInstManager getNextInstManager() {
        return MServerSkeleton.getInstance().getNextSystemModel().getInstanceManager();
    }
}
