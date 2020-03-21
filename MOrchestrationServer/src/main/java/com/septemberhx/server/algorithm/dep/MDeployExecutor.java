package com.septemberhx.server.algorithm.dep;

import com.septemberhx.server.model.MDeployManager;
import com.septemberhx.server.model.MSystemModel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/21
 */
public class MDeployExecutor {

    public static void execute(MDeployManager deployManager, MSystemModel currModel) {
        // todo: finish the deploy executor
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
