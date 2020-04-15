package com.septemberhx.common.bean.agent;

import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.MSvcInstance;
import com.septemberhx.common.service.dependency.PureSvcDependency;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/4/13
 */
@Getter
@Setter
@ToString
public class MDepResetRoutingBean {

    private Map<String, MService> serviceMap = new HashMap<>();
    private Map<String, MSvcInstance> instMap = new HashMap<>();

    private Map<String, List<PureSvcDependency>> nodeInstDepList = new HashMap<>();
    private Map<String, List<String>> nodeInstServiceIdList = new HashMap<>();

    private Map<String, List<PureSvcDependency>> nodeUserDepList = new HashMap<>();
    private Map<String, List<String>> nodeUserServiceIdList = new HashMap<>();

    public Map<String, Map<String, Integer>> nodeDelayMap = new HashMap<>();

    public void putInstValues(Map<String, Map<PureSvcDependency, String>> valueMap) {
        for (String nodeId : valueMap.keySet()) {
            List<PureSvcDependency> depList = new ArrayList<>();
            List<String> svcList = new ArrayList<>();
            for (PureSvcDependency dep : valueMap.get(nodeId).keySet()) {
                depList.add(dep);
                svcList.add(valueMap.get(nodeId).get(dep));
            }
            this.nodeInstDepList.put(nodeId, depList);
            this.nodeInstServiceIdList.put(nodeId, svcList);
        }
    }

    public void putUserValues(Map<String, Map<PureSvcDependency, String>> valueMap) {
        for (String nodeId : valueMap.keySet()) {
            List<PureSvcDependency> depList = new ArrayList<>();
            List<String> svcList = new ArrayList<>();
            for (PureSvcDependency dep : valueMap.get(nodeId).keySet()) {
                depList.add(dep);
                svcList.add(valueMap.get(nodeId).get(dep));
            }
            this.nodeUserDepList.put(nodeId, depList);
            this.nodeUserServiceIdList.put(nodeId, svcList);
        }
    }

    public Map<String, Map<PureSvcDependency, String>> instDepMap() {
        Map<String, Map<PureSvcDependency, String>> r = new HashMap<>();
        for (String nodeId : nodeInstDepList.keySet()) {
            Map<PureSvcDependency, String> depMap = new HashMap<>();
            for (int i = 0; i < nodeInstDepList.get(nodeId).size(); ++i) {
                depMap.put(nodeInstDepList.get(nodeId).get(i), nodeInstServiceIdList.get(nodeId).get(i));
            }
            r.put(nodeId, depMap);
        }
        return r;
    }

    public Map<String, Map<PureSvcDependency, String>> userDepMap() {
        Map<String, Map<PureSvcDependency, String>> r = new HashMap<>();
        for (String nodeId : nodeUserDepList.keySet()) {
            Map<PureSvcDependency, String> depMap = new HashMap<>();
            for (int i = 0; i < nodeUserDepList.get(nodeId).size(); ++i) {
                depMap.put(nodeUserDepList.get(nodeId).get(i), nodeUserServiceIdList.get(nodeId).get(i));
            }
            r.put(nodeId, depMap);
        }
        return r;
    }
}
