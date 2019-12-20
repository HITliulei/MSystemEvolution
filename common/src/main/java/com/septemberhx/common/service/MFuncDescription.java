package com.septemberhx.common.service;

import lombok.Getter;
import lombok.Setter;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2019/12/13
 */
@Getter
@Setter
public class MFuncDescription {

    private String functionName;

    /*
     * We can change the sla definition in the future
     */
    private int slaLevel;


    /**
     * Check whether this function can satisfy demand
     *
     * ATTENTION: we should use this kind of functions to avoid the *slaLevel* shown outside of this class
     *
     * @param description: the demand
     * @return Boolean
     */
    public boolean ifSatisfied(MFuncDescription description) {
        return false;
    }
}
