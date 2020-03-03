package com.septemberhx.server.model;

import com.septemberhx.common.base.MUniqueObjectManager;

import java.util.*;

public class MServiceInstanceManager extends MUniqueObjectManager<MServiceInstance> {

    public Map<String, List<MServiceInstance>> getInstancesGroupByClusterId() {
        Map<String, List<MServiceInstance>> resultMap = new HashMap<>();
        for (MServiceInstance instance : this.objectMap.values()) {
            if (!resultMap.containsKey(instance.getClusterId())) {
                resultMap.put(instance.getClusterId(), new ArrayList<>());
            }
            resultMap.get(instance.getClusterId()).add(instance);
        }
        return resultMap;
    }

    public Optional<MServiceInstance> getByClusterIdAndRegistryId(String clusterId, String registryId) {
        for (MServiceInstance instance : this.objectMap.values()) {
            if (instance.getClusterId().equals(clusterId) && instance.getRegistryId().equals(registryId)) {
                return Optional.of(instance);
            }
        }
        return Optional.empty();
    }
}