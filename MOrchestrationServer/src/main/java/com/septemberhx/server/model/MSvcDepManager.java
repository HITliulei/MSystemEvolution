package com.septemberhx.server.model;

import com.septemberhx.common.service.dependency.BaseSvcDependency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/3
 */
public class MSvcDepManager {

    // Map[service Id, List[dependency]]
    Map<String, List<BaseSvcDependency>> depMap;

    private MSvcDepManager() {
        this.depMap = new HashMap<>();
    }

    public List<BaseSvcDependency> getDependenciesBySvcId(String serviceId) {
        List<BaseSvcDependency> result = new ArrayList<>();
        if (depMap.containsKey(serviceId)) {
            result.addAll(depMap.get(serviceId));
        }
        return result;
    }

    public void updateDependenciesBySvcId(String svcId, List<BaseSvcDependency> deps) {
        this.depMap.put(svcId, deps);
    }
}
