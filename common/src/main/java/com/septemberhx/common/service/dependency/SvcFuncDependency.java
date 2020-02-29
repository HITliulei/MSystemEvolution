package com.septemberhx.common.service.dependency;

import com.septemberhx.common.service.MFuncDescription;
import lombok.Getter;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/2/29
 */
@Getter
public class SvcFuncDependency extends BaseSvcDependency {

    private MFuncDescription funcDescription;

    public SvcFuncDependency(String name, String funcName, int slaLevel) {
        this.funcDescription = new MFuncDescription(funcName, slaLevel);
        this.name = name;
    }

    @Override
    public String toString() {
        return "SvcFuncDependency{" +
                "funcDescription=" + funcDescription +
                ", name='" + name + '\'' +
                '}';
    }
}
