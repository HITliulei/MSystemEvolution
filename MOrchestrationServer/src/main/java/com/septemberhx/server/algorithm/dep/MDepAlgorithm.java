package com.septemberhx.server.algorithm.dep;

import com.septemberhx.common.base.node.MServerNode;
import com.septemberhx.common.service.MSvcInterface;
import com.septemberhx.common.service.dependency.BaseSvcDependency;
import com.septemberhx.server.model.MServiceInstance;
import com.septemberhx.server.model.MSystemModel;
import org.javatuples.Pair;

import java.util.*;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/7
 */
public class MDepAlgorithm {

    /**
     * Get the nearest instance that can satisfy the dependency
     * @param baseSvcDependency: dependency to meet
     * @param currModel: current system model
     * @param nodeId: which node the dependency occurs
     * @return Pair[instanceId, interfaceId] or empty
     */
    public static Optional<Pair<String, String>> getAvailableInstForDepRequest(
            BaseSvcDependency baseSvcDependency, MSystemModel currModel, String nodeId) {
        BaseSvcDependency svcDependency = baseSvcDependency.toRealDependency();

        List<MServerNode> nodeList = currModel.getNodeManager().getConnectedNodesDecentWithDelayTolerance(nodeId);
        List<String> nodeIdList = new ArrayList<>();
        nodeIdList.add(nodeId);
        for (MServerNode node : nodeList) {
            nodeIdList.add(node.getId());
        }

        Set<String> failedServiceIdSet = new HashSet<>();
        for (String targetNodeId : nodeIdList) {
            List<MServiceInstance> serviceInstanceList = currModel.getInstanceManager().getInstanceByNodeId(targetNodeId);
            for (MServiceInstance svcInstance : serviceInstanceList) {
                if (failedServiceIdSet.contains(svcInstance.getServiceId())) {
                    continue;
                }

                List<MSvcInterface> svcInterfaceList = currModel.getServiceManager().getInterfacesMetDep(
                        svcInstance.getServiceId(), svcDependency
                );
                if (svcInterfaceList.isEmpty()) {
                    failedServiceIdSet.add(svcInstance.getServiceId());
                } else {
                    for (MSvcInterface svcInterface : svcInterfaceList) {
                        if (currModel.getRoutingManager().checkInstHasAvailablePlot(
                                svcInstance.getId(),
                                svcInterface.getId(),
                                currModel.getServiceManager(),
                                currModel.getInstanceManager(),
                                1
                        )) {
                            return Optional.of(new Pair<>(svcInstance.getId(), svcInterface.getId()));
                        }
                    }
                }
            }
        }

        return Optional.empty();
    }
}
