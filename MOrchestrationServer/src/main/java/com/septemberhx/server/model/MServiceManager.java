package com.septemberhx.server.model;

import com.septemberhx.common.base.MUniqueObjectManager;
import com.septemberhx.common.service.MService;
import com.septemberhx.server.utils.MDatabaseUtils;
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
        return this.updateService(newService);
    }

    @Override
    public void update(MService obj) {
        throw new RuntimeException("Don't use this method in MServiceManager due to the DB operations");
    }

    public boolean updateService(MService newService) {
        MDatabaseUtils.databaseUtils.deleteService(newService);
        MDatabaseUtils.databaseUtils.insertService(newService);
        this.objectMap.put(newService.getId(), newService);
        return true;
    }

    public List<MService> getServicesByServiceName(String serviceName) {
        return this.objectMap.values().stream()
                .filter(s -> s.getServiceName().equals(serviceName)).collect(Collectors.toList());
    }

    public void updateImageUrl(String serviceId, String imageUrl) {
        if (this.containsById(serviceId)) {
            MDatabaseUtils.databaseUtils.updateServiceImageUrl(serviceId, imageUrl);
            this.getById(serviceId).get().setImageUrl(imageUrl);
        }
    }
}
