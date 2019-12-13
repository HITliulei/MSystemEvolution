package com.septemberhx.common.service;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2019/12/13
 *
 * Use this class to make it easier to change the rules of the version definition
 * Do not try to add setter methods for the variables to avoid bugs
 */
@Getter
public class MServiceVersion {

    private int mainVersionNum;
    private int childVersionNum;
    private int fixVersionNum;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MServiceVersion that = (MServiceVersion) o;
        return mainVersionNum == that.mainVersionNum &&
                childVersionNum == that.childVersionNum &&
                fixVersionNum == that.fixVersionNum;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mainVersionNum, childVersionNum, fixVersionNum);
    }

    @Override
    public String toString() {
        return String.format("%d.%d.%d", mainVersionNum, childVersionNum, fixVersionNum);
    }
}
