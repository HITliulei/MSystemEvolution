package com.septemberhx.server.model;

import com.septemberhx.common.base.MUniqueObjectManager;
import com.septemberhx.common.exception.MethodNotAllowException;
import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.MSvcInterface;
import com.septemberhx.common.service.MSvcVersion;
import com.septemberhx.common.service.dependency.*;
import com.septemberhx.server.utils.MDatabaseUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
//        MDatabaseUtils.databaseUtils.deleteService(newService);
//        MDatabaseUtils.databaseUtils.insertService(newService);
        this.objectMap.put(newService.getId(), newService);
        return true;
    }

    public List<MService> getServicesByServiceName(String serviceName) {
        return this.objectMap.values().stream()
                .filter(s -> s.getServiceName().equals(serviceName)).collect(Collectors.toList());
    }

    public List<MService> getServicesBySlaDep(PureSvcDependency slaDep) {
        List<MService> serviceList = this.getServicesByServiceName(slaDep.getServiceName());
        List<MService> resultList = new ArrayList<>();
        for (MService svc : serviceList) {
            Optional<MSvcInterface> apiOpt = svc.getInterfaceByPatternUrl(slaDep.getPatternUrl());
            if (apiOpt.isPresent() && slaDep.getSlaSet().contains(apiOpt.get().getFuncDescription().getSla())) {
                resultList.add(svc);
            }
        }
        return resultList;
    }

    public void updateImageUrl(String serviceId, String imageUrl) {
        Optional<MService> serviceOptional = this.getById(serviceId);
        if (serviceOptional.isPresent()) {
//            MDatabaseUtils.databaseUtils.updateServiceImageUrl(serviceId, imageUrl);
            serviceOptional.get().setImageUrl(imageUrl);
        }
    }

    public Optional<MService> getByServiceNameAndVersion(String serviceName, String version) {
        for (MService service : this.objectMap.values()) {
            if (service.getServiceName().toLowerCase().equals(serviceName.toLowerCase()) && service.getServiceVersion().toString().equals(version)) {
                return Optional.of(service);
            }
        }
        return Optional.empty();
    }

    public List<MSvcInterface> getInterfacesMetDep(String serviceId, BaseSvcDependency dependency) {
        List<MSvcInterface> svcInterfaceList = new ArrayList<>();
        Optional<MService> serviceOpt = this.getById(serviceId);
        if (serviceOpt.isPresent()) {
            for (MSvcInterface svcInterface : serviceOpt.get().getServiceInterfaceMap().values()) {
                if (this.checkMetDep(svcInterface, dependency)) {
                    svcInterfaceList.add(svcInterface);
                }
            }
        }
        return svcInterfaceList;
    }

    public List<MSvcInterface> getInterfacesMetDep(BaseSvcDependency dependency) {
        List<MSvcInterface> svcInterfaceList = new ArrayList<>();
        for (MService service : this.objectMap.values()) {
            for (MSvcInterface svcInterface : service.getServiceInterfaceMap().values()) {
                if (this.checkMetDep(svcInterface, dependency)) {
                    svcInterfaceList.add(svcInterface);
                }
            }
        }
        return svcInterfaceList;
    }

    /*
     * Check whether interface svcInterface1 is compatible with svcInterface2
     *
     * In fact, the compatible table should be calculated before other part for preparation
     *
     * todo: record the compatible table to speed up the program
     */
    public boolean checkIfCompatible(MSvcInterface svcInterface1, MSvcInterface svcInterface2) {
        if (svcInterface1.getId().equals(svcInterface2.getId())) {
            return true;
        }
        // todo: add more judgement for this task

        return false;
    }

    public Optional<MSvcInterface> getInterface(String serviceName, MSvcVersion svcVersion, String patternUrl) {
        Optional<MService> serviceOpt = this.getByServiceNameAndVersion(serviceName, svcVersion.toString());
        if (serviceOpt.isPresent()) {
            return serviceOpt.get().getInterfaceByPatternUrl(patternUrl);
        }
        return Optional.empty();
    }

    public boolean checkMetDep(MSvcInterface svcInterface, BaseSvcDependency baseSvcDependency) {
        BaseSvcDependency svcDependency = baseSvcDependency.toRealDependency();
        if (svcDependency instanceof SvcVerDependency) {
            Optional<MService> serviceOpt = this.getById(svcInterface.getServiceId());
            if (serviceOpt.isPresent()) {
                if (svcInterface.getServiceId().startsWith(svcDependency.getServiceName())
                        && svcInterface.getPatternUrl().equals(svcDependency.getPatternUrl())
                        && svcDependency.getVersionSet().contains(serviceOpt.get().getServiceVersion())) {
                    return true;
                }

                for (MSvcVersion svcVersion : svcDependency.getVersionSet()) {
                    Optional<MSvcInterface> svcInterfaceOpt = this.getInterface(
                            svcDependency.getServiceName(), svcVersion, svcDependency.getPatternUrl());
                    if (svcInterfaceOpt.isPresent() && this.checkIfCompatible(svcInterface, svcInterfaceOpt.get())) {
                        return true;
                    }
                }

            }
        } else if (svcDependency instanceof SvcSlaDependency) {
            return svcInterface.getServiceId().startsWith(svcDependency.getServiceName())
                    && svcInterface.getPatternUrl().equals(svcDependency.getPatternUrl())
                    && svcDependency.getSlaSet().contains(svcInterface.getFuncDescription().getSla());
        } else if (svcDependency instanceof SvcFuncDependency) {
            return svcInterface.getFuncDescription().getFunc().equals(svcDependency.getFunc())
                    && svcDependency.getSlaSet().contains(svcInterface.getFuncDescription().getSla());
        }
        return false;
    }
}
