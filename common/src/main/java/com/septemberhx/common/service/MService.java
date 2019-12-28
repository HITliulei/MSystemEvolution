package com.septemberhx.common.service;

import com.septemberhx.common.base.MUniqueObject;
import com.septemberhx.common.dao.MServiceDao;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

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
}
