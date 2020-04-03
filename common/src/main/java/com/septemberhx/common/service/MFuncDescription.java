package com.septemberhx.common.service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2019/12/13
 */
@Getter
@ToString
@Setter
public class MFuncDescription {

    private MFunc func;

    /*
     * We can change the sla definition in the future
     */
    private MSla sla;

    public MFuncDescription(String func, int sla) {
        this.func = new MFunc(func);
        this.sla = new MSla(sla);
    }

    /**
     * Check whether this function can satisfy demand
     *
     * ATTENTION: we should use this kind of functions to avoid the *slaLevel* shown outside of this class
     *
     * @param description: the demand
     * @return Boolean
     */
    public boolean ifSatisfied(MFuncDescription description) {
        return this.ifSatisfied(description.getFunc(), description.getSla());
    }

    public boolean ifSatisfied(MFunc func, MSla sla) {
        return this.func.ifSatisfied(func) && this.sla.ifSatisfied(sla);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MFuncDescription that = (MFuncDescription) o;
        return func.equals(that.func) &&
                sla.equals(that.sla);
    }

    @Override
    public int hashCode() {
        return Objects.hash(func, sla);
    }
}
