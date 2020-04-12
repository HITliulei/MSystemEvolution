package com.septemberhx.server.model;

import com.septemberhx.common.base.MResource;
import com.septemberhx.common.base.node.MServerNode;
import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.MSvcInstance;
import com.septemberhx.server.utils.MIDUtils;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;
import java.util.Optional;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/18
 */
@Getter
@ToString
public class MDeployManager {
    private MSvcInstManager instManager;
    private MClusterManager clusterManager;
    private MSvcManager svcManager;

    public MDeployManager(MClusterManager clusterManager, MSvcManager svcManager) {
        this.instManager = new MSvcInstManager();
        this.svcManager = svcManager;
        this.clusterManager = clusterManager;
    }

    public boolean checkIfNodeCanDeploy(String nodeId, MService svc) {
        MResource usedRes = this.getNodeResUsage(nodeId);
        Optional<MServerNode> nodeOpt = this.clusterManager.getNodeById(nodeId);
        return nodeOpt.map(node -> node.getResource().sub(usedRes).isEnough(svc.getResource())).orElse(false);
    }

    public boolean addInstForSvcOnNode(String nodeId, MService svc) {
        Optional<MServerNode> nodeOpt = this.clusterManager.getNodeById(nodeId);
        if (nodeOpt.isPresent() && this.checkIfNodeCanDeploy(nodeId, svc)) {
            MSvcInstance svcInst = new MSvcInstance(
                    null,
                    nodeOpt.get().getClusterId(),
                    nodeId,
                    null,
                    null,
                    MIDUtils.uniqueInstanceId(svc.getServiceName(), svc.getServiceVersion().toString()),
                    null,
                    svc.getServiceName(),
                    svc.getId(),
                    null,
                    svc.getServiceVersion().toString()
            );
            this.instManager.update(svcInst);
            return true;
        }
        return false;
    }

    public MResource getNodeResUsage(String nodeId) {
        MResource tmpR = new MResource();
        for (MSvcInstance inst : instManager.getInstanceByNodeId(nodeId)) {
            Optional<MService> svcOpt = this.svcManager.getById(inst.getServiceId());
            svcOpt.ifPresent(service -> tmpR.free(service.getResource()));
        }
        return tmpR;
    }

    public void deployInstOnClouds(Map<MService, Integer> instCountMap) {
        // todo: deploy instances on cloud cluster
    }
}
