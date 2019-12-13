package com.septemberhx.common.service;

import com.septemberhx.common.base.MUniqueObject;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2019/12/13
 */
@Getter
@Setter
public class MService extends MUniqueObject {

    /*
     * The service name is not the same as the serviceId.
     * For two services, they can have the same service name with different version.
     */
    private String serviceName;
    private Map<String, MServiceInterface> serviceInterfaceMap;
    private MServiceVersion serviceVersion;
    private String girUrl;
}
