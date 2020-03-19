package com.septemberhx.server.model;

import com.septemberhx.common.base.MUniqueObjectManager;
import com.septemberhx.common.service.MSvcInstance;

import java.util.*;
import java.util.stream.Collectors;

public class MSvcInstManager extends MUniqueObjectManager<MSvcInstance> {

    public Map<String, List<MSvcInstance>> getInstancesGroupByClusterId() {
        Map<String, List<MSvcInstance>> resultMap = new HashMap<>();
        for (MSvcInstance instance : this.objectMap.values()) {
            if (!resultMap.containsKey(instance.getClusterId())) {
                resultMap.put(instance.getClusterId(), new ArrayList<>());
            }
            resultMap.get(instance.getClusterId()).add(instance);
        }
        return resultMap;
    }

    public Optional<MSvcInstance> getByClusterIdAndRegistryId(String clusterId, String registryId) {
        for (MSvcInstance instance : this.objectMap.values()) {
            if (instance.getClusterId().equals(clusterId) && instance.getRegistryId().equals(registryId)) {
                return Optional.of(instance);
            }
        }
        return Optional.empty();
    }

    public List<MSvcInstance> getInstanceByNodeId(String nodeId) {
        return this.objectMap.values().stream()
                .filter(s -> s.getNodeId().equals(nodeId)).collect(Collectors.toList());
    }
}
