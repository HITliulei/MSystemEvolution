package com.septemberhx.server.algorithm.dep;

import com.septemberhx.common.base.node.MServerNode;
import com.septemberhx.common.bean.gateway.MDepRequestCacheBean;
import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.MSvcInterface;
import com.septemberhx.common.service.dependency.*;
import com.septemberhx.server.bean.MPredictBean;
import com.septemberhx.common.service.MSvcInstance;
import com.septemberhx.server.model.*;
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
    @Deprecated
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
            List<MSvcInstance> serviceInstanceList = currModel.getInstanceManager().getInstanceByNodeId(targetNodeId);
            for (MSvcInstance svcInstance : serviceInstanceList) {
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

    public static void updateDepCallCount(Map<String, Map<String, Map<BaseSvcDependency, Integer>>> svcId2Api2DepCount,
                                          Map<String, Map<String, Integer>> svcId2ApiCallCount, MSvcManager svcManager) {
        for (String svcId : svcId2ApiCallCount.keySet()) {
            Optional<MService> svcOpt = svcManager.getById(svcId);
            svcOpt.ifPresent(service -> {
                for (String patternUrl : svcId2ApiCallCount.get(svcId).keySet()) {
                    Optional<MSvcInterface> apiOpt = service.getInterfaceByPatternUrl(patternUrl);
                    apiOpt.ifPresent(svcInterface -> {
                        if (svcId2Api2DepCount.containsKey(svcId)
                                && svcId2Api2DepCount.get(svcId).containsKey(patternUrl)) {
                            for (BaseSvcDependency dependency : svcId2Api2DepCount.get(svcId).get(patternUrl).keySet()) {
                                int newCoe = svcId2Api2DepCount.get(svcId).get(patternUrl).get(dependency)
                                        / svcId2ApiCallCount.get(svcId).get(patternUrl);
                                svcInterface.getInvokeCountMap().put(dependency.hashCode(), newCoe);
                            }
                        }
                    });
                }
            });
        }
    }

    /*
     * Get the deploy topology of next few time windows, including what instances should be deployed on each node
     */
    public static MDeployManager getSuggestedTopology(
            Map<String, Map<PureSvcDependency, Integer>> demandCountMap,
            Map<String, Map<PureSvcDependency, Integer>> userDepMap, MSvcManager svcManager,
            MClusterManager clusterManager, String clusterId) {

        Map<String, Pair<Map<MService, Integer>, Map<BaseSvcDependency, MService>>> nodeInfoList = new HashMap<>();

        // calculate the svc set and how many users each service should serve at the same time window
        for (String nodeId : demandCountMap.keySet()) {
            // calculate the smallest service set that can satisfy the demands on this node without the dependency
            Map<PureSvcDependency, MService> svcResult = MappingSvcAlgos.mappingFuncDepList(
                    demandCountMap.get(nodeId), demandCountMap.get(nodeId).keySet());

            // calculate the smallest service set with dependency
            Map<BaseSvcDependency, MService> svcTree = MappingSvcAlgos.buildSvcTree(new HashSet<>(svcResult.values()));

            // calculate how many users each of the service set should serve at the same time
            Map<MService, Integer> svcUserCount = MappingSvcAlgos.calcSvcUserCount(
                    new ArrayList<>(svcTree.values()), svcTree, svcResult, userDepMap.get(nodeId));

            // record the info about one node
            nodeInfoList.put(nodeId, new Pair<>(svcUserCount, svcTree));
        }

        // use the service set with dependency and the user count of each service to create the topology
        return MDeployAlgos.calcDeployTopology(nodeInfoList, svcManager, clusterManager, clusterId);
    }
}
