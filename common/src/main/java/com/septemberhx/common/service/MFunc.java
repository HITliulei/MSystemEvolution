package com.septemberhx.common.service;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/2/29
 *
 * The function description for an API.
 *
 * This class is created for possible future extension
 */
@Getter
@Setter
public class MFunc {

    private String functionName;

    public MFunc(String functionName) {
        this.functionName = functionName;
    }

    public MFunc(MFunc func) {
        this.functionName = func.functionName;
    }

    public boolean ifSatisfied(MFunc func) {
        return this.functionName.equals(func.functionName);
    }

    @Override
    public String toString() {
        return "MFunction{" +
                "functionName='" + functionName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MFunc mFunc = (MFunc) o;
        return functionName.equals(mFunc.functionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(functionName);
    }
}
