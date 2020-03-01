package com.septemberhx.common.service.dependency;

import lombok.Getter;
import lombok.ToString;

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
@ToString
public class MSvcDepDesc {

    // the unique id of the service with version that this dependency belongs to
    private String serviceId;

    // the list means the service may have multiple kinds of dependency
    // the map means the service may depend on multiple services
    private List<Map<String, BaseSvcDependency>> dependencyList;

    // unique dependency name for this service
    private String name;

    public MSvcDepDesc(String serviceId, String name, List<Map<String, BaseSvcDependency>> dependencyList) {
        this.serviceId = serviceId;
        this.name = name;
        this.dependencyList = dependencyList;
    }
}
