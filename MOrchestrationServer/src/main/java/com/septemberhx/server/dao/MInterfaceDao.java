package com.septemberhx.server.dao;

import com.septemberhx.common.service.MFuncDescription;
import com.septemberhx.common.service.MServiceInterface;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2019/12/28
 */
@Getter
@Setter
@ToString
public class MInterfaceDao {
    private String id;
    private String patternUrl;
    private String requestMethod;
    private String returnType;
    private String serviceId;
    private String functionName;
    private String featureName;
    private Integer slaLevel;

    public MInterfaceDao(String id, String patternUrl, String functionName, String requestMethod, String returnType, String serviceId, String featureName, Integer slaLevel) {
        this.id = id;
        this.functionName = functionName;
        this.patternUrl = patternUrl;
        this.requestMethod = requestMethod;
        this.returnType = returnType;
        this.serviceId = serviceId;
        this.featureName = featureName;
        this.slaLevel = slaLevel;
    }

    public MInterfaceDao() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MInterfaceDao that = (MInterfaceDao) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(patternUrl, that.patternUrl) &&
                Objects.equals(requestMethod, that.requestMethod) &&
                Objects.equals(returnType, that.returnType) &&
                Objects.equals(serviceId, that.serviceId) &&
                Objects.equals(functionName, that.functionName) &&
                Objects.equals(featureName, that.featureName) &&
                Objects.equals(slaLevel, that.slaLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, patternUrl, requestMethod, returnType, serviceId, functionName, featureName, slaLevel);
    }

    public static MInterfaceDao fromDto(MServiceInterface serviceInterface) {
        String featureName = null;
        Integer slaLevel = null;
        if (serviceInterface.getFuncDescription() != null) {
            featureName = serviceInterface.getFuncDescription().getFeatureName();
            slaLevel = serviceInterface.getFuncDescription().getSlaLevel();
        }

        return new MInterfaceDao(
            serviceInterface.getId(),
            serviceInterface.getPatternUrl(),
            serviceInterface.getFunctionName(),
            serviceInterface.getRequestMethod(),
            serviceInterface.getReturnType(),
            serviceInterface.getServiceId(),
            featureName,
            slaLevel
        );
    }

    public MServiceInterface toDto() {
        MServiceInterface serviceInterface = new MServiceInterface();
        serviceInterface.setId(this.id);
        serviceInterface.setPatternUrl(this.patternUrl);
        serviceInterface.setFunctionName(this.functionName);
        serviceInterface.setRequestMethod(this.requestMethod);
        serviceInterface.setReturnType(this.returnType);
        serviceInterface.setServiceId(this.serviceId);

        MFuncDescription funcDescription = new MFuncDescription();
        funcDescription.setFeatureName(this.featureName);
        funcDescription.setSlaLevel(this.slaLevel);
        serviceInterface.setFuncDescription(funcDescription);

        return serviceInterface;
    }
}
