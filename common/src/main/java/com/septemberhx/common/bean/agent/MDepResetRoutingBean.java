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

    private Map<String, List<PureSvcDependency>> nodeDepList = new HashMap<>();
    private Map<String, List<String>> nodeServiceIdList = new HashMap<>();

    public void putValues(Map<String, Map<PureSvcDependency, String>> valueMap) {
        for (String nodeId : valueMap.keySet()) {
            List<PureSvcDependency> depList = new ArrayList<>();
            List<String> svcList = new ArrayList<>();
            for (PureSvcDependency dep : valueMap.get(nodeId).keySet()) {
                depList.add(dep);
                svcList.add(valueMap.get(nodeId).get(dep));
            }
            this.nodeDepList.put(nodeId, depList);
            this.nodeServiceIdList.put(nodeId, svcList);
        }
    }

    public Map<String, Map<PureSvcDependency, String>> depMap() {
        Map<String, Map<PureSvcDependency, String>> r = new HashMap<>();
        for (String nodeId : nodeDepList.keySet()) {
            Map<PureSvcDependency, String> depMap = new HashMap<>();
            for (int i = 0; i < nodeDepList.get(nodeId).size(); ++i) {
                depMap.put(nodeDepList.get(nodeId).get(i), nodeServiceIdList.get(nodeId).get(i));
            }
            r.put(nodeId, depMap);
        }
        return r;
    }
}
