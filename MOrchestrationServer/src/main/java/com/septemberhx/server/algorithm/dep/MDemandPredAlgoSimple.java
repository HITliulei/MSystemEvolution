package com.septemberhx.server.algorithm.dep;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.septemberhx.common.service.dependency.PureSvcDependency;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/4/16
 */
public class MDemandPredAlgoSimple {

    private static MDemandPredAlgoSimple inst = null;
    private static final String dataJson = "/root/data.json";

    private Map<Integer, Map<String, Map<PureSvcDependency, Integer>>> demandCountMap;

    private MDemandPredAlgoSimple() {
        this.demandCountMap = new HashMap<>();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(dataJson));

            Gson gson = new GsonBuilder().create();
            JsonObject jsonObject = gson.fromJson(bufferedReader, JsonObject.class);

            for (Map.Entry<String, JsonElement> oneD : jsonObject.getAsJsonObject("demands").entrySet()) {
                Integer time = Integer.valueOf(oneD.getKey());
                demandCountMap.put(time, new HashMap<>());

                for (Map.Entry<String, JsonElement> nodeInfo : oneD.getValue().getAsJsonObject().entrySet()) {
                    String nodeId = nodeInfo.getKey();
                    demandCountMap.get(time).put(nodeId, new HashMap<>());
                    for (int i = 0; i < nodeInfo.getValue().getAsJsonArray().size(); ++i) {
                        JsonElement jsonElement = nodeInfo.getValue().getAsJsonArray().get(i);
                        PureSvcDependency dep = gson.fromJson(jsonElement, PureSvcDependency.class);
                        demandCountMap.get(time).get(nodeId).put(
                                dep,
                                jsonObject.getAsJsonObject("count").getAsJsonObject(oneD.getKey()).getAsJsonArray(nodeId).get(i).getAsInt());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(this.demandCountMap.size());
    }

    private Map<String, Map<PureSvcDependency, Integer>> getFutureCount(int startTimeInMin, int endTimeInMin) {
        Map<String, Map<PureSvcDependency, Integer>> resultM = new HashMap<>();
        for (int i = startTimeInMin; i < endTimeInMin; ++i) {
            for (String nodeId : this.demandCountMap.get(i).keySet()) {
                if (!resultM.containsKey(nodeId)) {
                    resultM.put(nodeId, new HashMap<>());
                }

                for (PureSvcDependency dep : this.demandCountMap.get(i).get(nodeId).keySet()) {
                    if (!resultM.get(nodeId).containsKey(dep)) {
                        resultM.get(nodeId).put(dep, 0);
                    }
                    resultM.get(nodeId).put(dep, Math.max(
                            resultM.get(nodeId).get(dep),
                            this.demandCountMap.get(i).get(nodeId).get(dep)
                    ));
                }
            }
        }
        return resultM;
    }

    public static synchronized MDemandPredAlgoSimple getInst() {
        if (inst == null) {
            inst = new MDemandPredAlgoSimple();
        }
        return inst;
    }

    public static void main(String[] args) {
        MDemandPredAlgoSimple.getInst();
        Map<String, Map<PureSvcDependency, Integer>> r = MDemandPredAlgoSimple.getInst().getFutureCount(0, 5);
        System.out.println(r.size());
    }
}
