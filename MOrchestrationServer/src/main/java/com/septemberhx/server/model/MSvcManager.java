package com.septemberhx.server.model;

import com.septemberhx.common.base.MUniqueObjectManager;
import com.septemberhx.common.exception.MethodNotAllowException;
import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.MSvcInterface;
import com.septemberhx.server.utils.MDatabaseUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2019/12/13
 */
@Component
public class MSvcManager extends MUniqueObjectManager<MService> {

    public boolean registerService(MService newService) {
        return this.updateService(newService);
    }

    @Override
    public void update(MService obj) {
        throw new MethodNotAllowException("Don't use this method in MServiceManager due to the DB operations");
    }

    public Optional<MSvcInterface> getInterfaceById(String interfaceId) {
        for (MService service : this.objectMap.values()) {
            if (service.getServiceInterfaceMap().containsKey(interfaceId)) {
                return Optional.of(service.getServiceInterfaceMap().get(interfaceId));
            }
        }
        return Optional.empty();
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
        Optional<MService> serviceOptional = this.getById(serviceId);
        if (serviceOptional.isPresent()) {
            MDatabaseUtils.databaseUtils.updateServiceImageUrl(serviceId, imageUrl);
            serviceOptional.get().setImageUrl(imageUrl);
        }
    }

    public Optional<MService> getByServiceNameAndVersion(String serviceName, String version) {
        for (MService service : this.objectMap.values()) {
            if (service.getServiceName().equals(serviceName) && service.getServiceVersion().toString().equals(version)) {
                return Optional.of(service);
            }
        }
        return Optional.empty();
    }
}
