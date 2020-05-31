package com.septemberhx.common.service;

import com.septemberhx.common.base.MUniqueObject;
import com.septemberhx.common.service.dependency.BaseSvcDependency;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

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
    private Map<Integer, Integer> invokeCountMap = new HashMap<>();

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

    public MSvcInterface() {}

    public MSvcInterface(MSvcInterface other) {
        this.patternUrl = other.patternUrl;
        this.funcDescription = new MFuncDescription(other.funcDescription);
        this.functionName = other.functionName;
        this.requestMethod = other.requestMethod;
        this.params = new ArrayList<>(other.params);
        this.returnType = other.returnType;
        this.serviceId = other.serviceId;
        this.invokeCountMap = new HashMap<>();
    }

    public void updateDeps(List<BaseSvcDependency> deps) {
        this.invokeCountMap.clear();

        for (BaseSvcDependency dep : deps) {
            this.invokeCountMap.put(dep.hashCode(), 1);
        }
    }
}
