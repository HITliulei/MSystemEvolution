package com.septemberhx.server.algorithm.dep;

import com.septemberhx.common.base.node.MServerNode;
import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.dependency.BaseSvcDependency;
import com.septemberhx.common.service.dependency.PureSvcDependency;
import com.septemberhx.server.model.MClusterManager;
import com.septemberhx.server.model.MDeployManager;
import com.septemberhx.server.model.MSvcManager;
import com.septemberhx.server.model.MSystemModel;
import org.javatuples.Pair;

import java.util.*;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/18
 */
public class MDeployAlgos {


    public static Map<MService, Integer> calcDeployTopologyOnOneNode(
            Map<MService, Integer> svcUserCount, Map<PureSvcDependency, MService> svcDepMap, String nodeId, MDeployManager deployManager) {
        Map<MService, Double> svcScore = new HashMap<>();
        List<MService> svcList = new ArrayList<>();
        Map<MService, Integer> svcInstSize = new HashMap<>();

        for (MService svc : svcUserCount.keySet()) {
            if (svcUserCount.get(svc) == 0) {
                continue;
            }

            // todo: the score ranking of service resource usage
            svcInstSize.put(svc, (int) Math.ceil(svcUserCount.get(svc) * 1.0 / svc.getMaxPlotNum()));
            svcScore.put(svc, svcUserCount.get(svc) * 1.0 / svc.getResource().getCpu() / svcInstSize.get(svc));
            svcList.add(svc);
        }
        svcList.sort((o1, o2) -> Double.compare(svcScore.get(o2), svcScore.get(o1)));

        Map<MService, Integer> undeployedUserSize = new HashMap<>(svcUserCount);
        boolean ifFailed = false;
        for (int i = 0; i < svcList.size(); ++i) {
            for (int n = 0; n < svcInstSize.get(svcList.get(i)); ++n) {
                if (!deployManager.addInstForSvcOnNode(nodeId, svcList.get(i))) {
                    ifFailed = true;
                    break;
                } else {
                    undeployedUserSize.put(
                            svcList.get(i), undeployedUserSize.get(svcList.get(i)) - svcList.get(i).getMaxPlotNum()
                    );
                }
            }

            if (ifFailed) {
                break;
            }
        }

        return undeployedUserSize;
    }

    /*
     * nodeInfoList: Map {
     *                  nodeId : <
     *                      Service User count map on each node
     *                      Service dependency routing mapping on each node
     *                  >
     *              }
     */
    public static MDeployManager calcDeployTopology(
            Map<String, Pair<Map<MService, Integer>, Map<PureSvcDependency, MService>>> nodeInfoList,
            MSvcManager svcManager, MClusterManager clusterManager, String clusterId) {
        MDeployManager deployManager = new MDeployManager(clusterManager, svcManager);

        Map<MService, Integer> unSolvedUserCountMap = new HashMap<>();
        // 1. solve each node separately
        for (String nodeId : nodeInfoList.keySet()) {
            Map<MService, Integer> unsolvedMap = calcDeployTopologyOnOneNode(
                    nodeInfoList.get(nodeId).getValue0(),
                    nodeInfoList.get(nodeId).getValue1(),
                    nodeId,
                    deployManager
            );
            if (!unsolvedMap.isEmpty()) {
                for (MService svc : unsolvedMap.keySet()) {
                    unSolvedUserCountMap.put(svc, unsolvedMap.get(svc) + unSolvedUserCountMap.getOrDefault(svc, 0));
                }
            }
        }

        // 2. solve unsolved instances by creating one
        Map<MService, Integer> instCountMap = new HashMap<>();
        for (MService svc : unSolvedUserCountMap.keySet()) {
            if (unSolvedUserCountMap.get(svc) > 0) {
                instCountMap.put(svc, (int) Math.ceil(unSolvedUserCountMap.get(svc) * 1.0 / svc.getMaxPlotNum()));
            }
        }
        List<MService> svcList = new ArrayList<>(instCountMap.keySet());
        svcList.sort((o1, o2) -> -Double.compare(
                o1.getMaxPlotNum() * 1.0 / o1.getResource().getCpu(),
                o2.getMaxPlotNum() * 1.0 / o2.getResource().getCpu()));

        for (MServerNode node : clusterManager.getNodesByClusterId(clusterId)) {
            for (MService svc : svcList) {
                if (instCountMap.get(svc) > 0) {
                    if (deployManager.addInstForSvcOnNode(node.getId(), svc)) {
                        instCountMap.put(svc, instCountMap.get(svc) - 1);
                    }
                }
            }
        }
        for (MService svc : instCountMap.keySet()) {
            if (instCountMap.get(svc) <= 0) {
                instCountMap.remove(svc);
            }
        }

        // 3. the remaining unsolved instance will be created on cloud side
        deployManager.deployInstOnClouds(instCountMap);
        return deployManager;
    }

    public static Map<String, Map<String, Integer>> diff(MDeployManager deployManager, MSystemModel currModel) {
        Map<String, Map<String, Integer>> nextInstMap = deployManager.getInstManager().getSvcInstMap();
        Map<String, Map<String, Integer>> currInstMap = currModel.getInstanceManager().getSvcInstMap();

        Map<String, Map<String, Integer>> diffMap = new HashMap<>();
        for (String currNodeId : currInstMap.keySet()) {
            if (!diffMap.containsKey(currNodeId)) {
                diffMap.put(currNodeId, new HashMap<>());
            }

            if (nextInstMap.containsKey(currNodeId)) {
                for (String svcId : currInstMap.get(currNodeId).keySet()) {
                    diffMap.get(currNodeId).put(svcId,
                            nextInstMap.get(currNodeId).getOrDefault(svcId, 0) - currInstMap.get(currNodeId).get(svcId));
                }

                for (String svcId : nextInstMap.get(currNodeId).keySet()) {
                    if (!currInstMap.get(currNodeId).containsKey(svcId)) {
                        diffMap.get(currNodeId).put(svcId, nextInstMap.get(currNodeId).get(svcId));
                    }
                }
            } else {
                for (String svcId : currInstMap.get(currNodeId).keySet()) {
                    diffMap.get(currNodeId).put(svcId, -currInstMap.get(currNodeId).get(svcId));
                }
            }
        }

        for (String nextNodeId : nextInstMap.keySet()) {
            if (!currInstMap.containsKey(nextNodeId)) {
                diffMap.put(nextNodeId, nextInstMap.get(nextNodeId));
            }
        }

        return diffMap;
    }
}
