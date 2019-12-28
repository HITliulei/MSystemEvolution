package com.septemberhx.common.service;

import com.septemberhx.common.base.MUniqueObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;
import java.util.Objects;

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
    private MServiceVersion serviceVersion;
    private String gitUrl;
    private int port;
    private String imageUrl;
    private Map<String, MServiceInterface> serviceInterfaceMap;

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
