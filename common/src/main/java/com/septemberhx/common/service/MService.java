package com.septemberhx.common.service;

import com.septemberhx.common.base.MUniqueObject;
import com.septemberhx.common.service.dependency.BaseSvcDependency;
import com.septemberhx.common.service.dependency.MSvcDepDesc;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2019/12/13
 */
@Getter
@Setter
@ToString
public class MService extends MUniqueObject {
    /*
     * The service name is not the same as the serviceId.
     * For two services, they can have the same service name with different version.
     */
    private String serviceName;
    private MSvcVersion serviceVersion;
    private String gitUrl;
    private int port;
    private String imageUrl;
    private Integer maxPlotNum;
    private Map<String, MSvcInterface> serviceInterfaceMap;
    private MSvcDepDesc mSvcDepDesc;

    /*
     * Get the interface list that use the given dependency
     */
    public List<MSvcInterface> getInterfacesContainDep(BaseSvcDependency dependency) {
        return this.serviceInterfaceMap.values().stream()
                .filter(s -> s.getInvokeCountMap().containsKey(dependency)).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MService service = (MService) o;

        if (this.serviceInterfaceMap.size() != service.serviceInterfaceMap.size()) {
            return false;
        }
        for (String interfaceId : this.serviceInterfaceMap.keySet()) {
            if (!this.serviceInterfaceMap.get(interfaceId).equals(service.serviceInterfaceMap.get(interfaceId))) {
                return false;
            }
        }

        return port == service.port &&
                Objects.equals(serviceName, service.serviceName) &&
                Objects.equals(serviceVersion, service.serviceVersion) &&
                Objects.equals(gitUrl, service.gitUrl) &&
                Objects.equals(imageUrl, service.imageUrl);
    }
}
