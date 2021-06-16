package com.septemberhx.common.service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
@Setter
@ToString
public class MSvcVersion {

    private int mainVersionNum;
    private int childVersionNum;
    private int fixVersionNum;

    private static Logger logger = LogManager.getLogger(MSvcVersion.class);

    @Override
    public boolean equals(Object o) {
        if (this == o){ return true;}
        if (o == null || getClass() != o.getClass()){ return false;}
        MSvcVersion that = (MSvcVersion) o;
        return mainVersionNum == that.mainVersionNum &&
                childVersionNum == that.childVersionNum &&
                fixVersionNum == that.fixVersionNum;
    }

    public static MSvcVersion fromStr(String versionStr) {
        if(versionStr == null){
            return null;
        }
        String[] numArr = versionStr.trim().replaceAll("[a-zA-Z]", "").split("\\.");
        if (numArr.length != 3) {
            throw new RuntimeException("Illegal version: " + versionStr);
        }

        MSvcVersion version = new MSvcVersion();
        try {
            version.setMainVersionNum(Integer.valueOf(numArr[0]));
            version.setChildVersionNum(Integer.valueOf(numArr[1]));
            version.setFixVersionNum(Integer.valueOf(numArr[2]));
        } catch (Exception e) {
            logger.debug(e);
        }
        return version;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mainVersionNum, childVersionNum, fixVersionNum);
    }

    @Override
    public String toString() {
        return String.format("%d.%d.%d", mainVersionNum, childVersionNum, fixVersionNum);
    }

    public String toCommonStr() {
        return String.format("v%s", this.toString());
    }
}
