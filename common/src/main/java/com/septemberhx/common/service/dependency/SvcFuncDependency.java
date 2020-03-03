package com.septemberhx.common.service.dependency;

import com.septemberhx.common.service.MFuncDescription;
import lombok.Getter;
import lombok.ToString;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/2/29
 */
@Getter
@ToString
public class SvcFuncDependency extends BaseSvcDependency {

    public SvcFuncDependency(String id, String funcName, int slaLevel) {
        this.id = id;
        this.funcDescription = new MFuncDescription(funcName, slaLevel);
    }

    public SvcFuncDependency(String id, MFuncDescription funcDescription) {
        this.id = id;
        this.funcDescription = funcDescription;
    }
}