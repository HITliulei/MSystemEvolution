package com.septemberhx.common.service.dependency;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/2/29
 *
 * The dependency description for service
 */
@Getter
@Setter
@ToString
public class MSvcDepDesc {

    // the unique id of the service with version that this dependency belongs to
    private String serviceId;

    // the list means the service may have multiple kinds of dependency
    // the map means the service may depend on multiple services
    private Map<String, Map<String, BaseSvcDependency>> dependencyMaps;

    // unique dependency name for this service
    private String name;

    public MSvcDepDesc() {}

    public MSvcDepDesc(String serviceId, String name, Map<String, Map<String, BaseSvcDependency>> dependencyMaps) {
        this.serviceId = serviceId;
        this.name = name;
        this.dependencyMaps = dependencyMaps;
    }

    public List<BaseSvcDependency> allDepList() {
        List<BaseSvcDependency> resultList = new ArrayList<>();
        dependencyMaps.values().stream().map(Map::values).forEach(resultList::addAll);
        return resultList;
    }
}
