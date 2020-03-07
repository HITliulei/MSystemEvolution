package com.septemberhx.common.service;

import com.septemberhx.common.base.MUniqueObject;
import com.septemberhx.common.service.dependency.BaseSvcDependency;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
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
public class MSvcInterface extends MUniqueObject {
    private String patternUrl;
    private MFuncDescription funcDescription;
    private String functionName;
    private String requestMethod;
    private List<MParamer> params;
    private String returnType;
    private String serviceId;
    //private List<MDependency> mDependencies;
    private Map<BaseSvcDependency, Integer> invokeCountMap;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MSvcInterface that = (MSvcInterface) o;

        if (this.params.size() != that.params.size()) return false;
        for (int i = 0; i < this.params.size(); ++i) {
            if (!this.params.get(i).equals(that.params.get(i))) return false;
        }

        return Objects.equals(patternUrl, that.patternUrl) &&
                Objects.equals(funcDescription, that.funcDescription) &&
                Objects.equals(functionName, that.functionName) &&
                Objects.equals(requestMethod, that.requestMethod) &&
                Objects.equals(returnType, that.returnType) &&
                Objects.equals(serviceId, that.serviceId);
    }
}
