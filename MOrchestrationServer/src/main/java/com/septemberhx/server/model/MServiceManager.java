package com.septemberhx.server.model;

import com.septemberhx.common.base.MUniqueObjectManager;
import com.septemberhx.common.service.MService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2019/12/13
 */
@Component
public class MServiceManager extends MUniqueObjectManager<MService> {

    public boolean registerService(MService newService) {
        boolean resultFlag = false;
        if (this.containsById(newService.getId())) {
            resultFlag = this.updateService(newService);
        } else {

        }
        return resultFlag;
    }

    public boolean updateService(MService newService) {
        return false;
    }

    public List<MService> getServicesByServiceName(String serviceName) {
        return this.objectMap.values().stream()
                .filter(s -> s.getServiceName().equals(serviceName)).collect(Collectors.toList());
    }
}
