package com.septemberhx.mgateway.core;

import com.septemberhx.common.service.MService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/7
 */
public class MGatewayInfo {
    private static MGatewayInfo instance;

    private Map<String, MService> serviceMap;

    private MGatewayInfo() {
        this.serviceMap = new HashMap<>();
    }

    public void updateService(MService service) {
        this.serviceMap.put(service.getId(), service);
    }

    public Optional<MService> getServiceByNameAndVer(String name, String verStr) {
        for (MService service : serviceMap.values()) {
            if (service.getServiceName().equals(name) && service.getServiceVersion().toString().equals(verStr)) {
                return Optional.of(service);
            }
        }
        return Optional.empty();
    }

    public static MGatewayInfo inst() {
        if (MGatewayInfo.instance == null) {
            synchronized (MGatewayInfo.class) {
                MGatewayInfo.instance = new MGatewayInfo();
            }
        }
        return MGatewayInfo.instance;
    }
}
