package com.septemberhx.server.algorithm.dep;

import com.septemberhx.common.base.node.MServerNode;
import com.septemberhx.common.bean.gateway.MDepRequestCacheBean;
import com.septemberhx.common.service.MSvcInterface;
import com.septemberhx.common.service.MSvcVersion;
import com.septemberhx.common.service.dependency.*;
import com.septemberhx.server.bean.MPredictBean;
import com.septemberhx.server.model.MServiceInstance;
import com.septemberhx.server.model.MSvcManager;
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

    /*
     * Main part of the evolution algorithm. Greedy algorithm will be used to help find the solution
     */
    public static void evolve(MPredictBean nextDemands, Map<String, List<MDepRequestCacheBean>> unMetDemandsOnEachNode) {
        // todo: implement the evolve algorithm
    }

    /*
     * This function serves for only one server node. The demandList should be the demands in one node.
     *   A minimum service tree will be found according to the demands. The principles are listed below:
     *   1. the dependencies should be reused as much as possible
     *        i.e., if d1 can be satisfied by more restricted d2, than d1 should be replaced by d2
     *   2. dependencies should be replaced by identified services instead of new one from repo
     *   3. dependencies should be replaced by services with least dependencies
     */
    public static Map<String, Map<PureSvcDependency, String>> getMinSpanningSvcTree(
            Map<PureSvcDependency, Integer> demandCountMap, MSvcManager svcManager) {

        // 1. merge dependencies as much as possible. The less restricted one is always merged to restricted one
        //      i.e.: d1 = pay function with sla 1, d2 = pay function with sla 1 or 2; than 2 will be merged to 1
        Map<PureSvcDependency, PureSvcDependency> mergedMap = new HashMap<>();
        List<PureSvcDependency> verDepList = new ArrayList<>();
        Set<PureSvcDependency> slaDepSet = new HashSet<>();
        Set<PureSvcDependency> funcDepSet = new HashSet<>();

        for (PureSvcDependency svcDependency : demandCountMap.keySet()) {
            BaseSvcDependency dependency = BaseSvcDependency.tranPure(svcDependency);
            if (dependency instanceof SvcVerDependency) {
                verDepList.add(svcDependency);
            } else if (dependency instanceof SvcSlaDependency) {
                slaDepSet.add(svcDependency);
            } else if (dependency instanceof SvcFuncDependency) {
                funcDepSet.add(svcDependency);
            }
        }

        // 1.1 merge SvcVerDependency, the most restricted

        // 1.2 merge SvcSlaDependency

        // 1.3 merge SvcFuncDependency, the last restricted

        // todo: finish the min svc tree algorithm

        Map<String, Map<PureSvcDependency, String>> resultMap = new HashMap<>();
        return resultMap;
    }

    public static Map<PureSvcDependency, PureSvcDependency> mergeVerDepList(
            Map<PureSvcDependency, Integer> demandCountMap, List<PureSvcDependency> verDepList) {
        // todo: implement the merge ver dep function
        Map<PureSvcDependency, PureSvcDependency> resultList = new HashMap<>();
        return resultList;
    }

    public static Pair<Map<PureSvcDependency, PureSvcDependency>, Map<PureSvcDependency, Integer>>
        mergeSameSvcPatternUrlList(Map<PureSvcDependency, Integer> demandCountMap, List<PureSvcDependency> verDepList) {
        Map<PureSvcDependency, Integer> demandCountMapCopy = new HashMap<>(demandCountMap);
        Map<PureSvcDependency, PureSvcDependency> resultMap = new HashMap<>();

        while (!verDepList.isEmpty()) {
            verDepList.sort(Comparator.comparingInt(demandCountMapCopy::get));
            PureSvcDependency currDep = verDepList.get(verDepList.size() - 1);
            Set<PureSvcDependency> intersectedSet = new HashSet<>();
            intersectedSet.add(currDep);
            Set<MSvcVersion> tmpVerSet = currDep.getVersionSet();
            for (int j = verDepList.size() - 2; j >= 0; --j) {
                Set<MSvcVersion> intersectionResult = getSetIntersection(
                        tmpVerSet, verDepList.get(j).getVersionSet());
                if (intersectionResult.isEmpty()) {
                    continue;
                }

                intersectedSet.add(verDepList.get(j));
                tmpVerSet = intersectionResult;
            }

            PureSvcDependency replaceDep = new PureSvcDependency(
                    null,
                    currDep.getServiceName(),
                    null,
                    currDep.getPatternUrl(),
                    tmpVerSet
            );
            int replacedCount = 0;
            for (PureSvcDependency tmpDep : intersectedSet) {
                verDepList.remove(tmpDep);
                replacedCount += demandCountMapCopy.get(tmpDep);
                demandCountMapCopy.remove(tmpDep);
                resultMap.put(tmpDep, replaceDep);
            }
            demandCountMapCopy.put(replaceDep, replacedCount);
        }
        return new Pair<>(resultMap, demandCountMapCopy);
    }

    public static <T> Set<T> getSetIntersection(Set<T> s1, Set<T> s2) {
        Set<T> tmpSet = new HashSet<>(s1);
        tmpSet.retainAll(s2);
        return tmpSet;
    }
}
