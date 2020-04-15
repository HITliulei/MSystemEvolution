package com.septemberhx.common.service;

import lombok.Getter;
import lombok.Setter;
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
public class MSvcVersion implements Comparable<MSvcVersion> {

    private int mainVersionNum;
    private int childVersionNum;
    private int fixVersionNum;

    private static Logger logger = LogManager.getLogger(MSvcVersion.class);

    public MSvcVersion(int mainVersionNum, int childVersionNum, int fixVersionNum) {
        this.mainVersionNum = mainVersionNum;
        this.childVersionNum = childVersionNum;
        this.fixVersionNum = fixVersionNum;
    }

    public MSvcVersion() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MSvcVersion that = (MSvcVersion) o;
        return mainVersionNum == that.mainVersionNum &&
                childVersionNum == that.childVersionNum &&
                fixVersionNum == that.fixVersionNum;
    }

    public static MSvcVersion fromStr(String versionStr) {
        String[] numArr = versionStr.split("\\.");
        if (numArr.length != 3) {
            throw new RuntimeException("Illegal version: " + versionStr);
        }

        MSvcVersion version = null;
        try {
            version = new MSvcVersion(
                    Integer.parseInt(numArr[0]), Integer.parseInt(numArr[1]), Integer.parseInt(numArr[2]));
        } catch (Exception e) {
            logger.info(e);
        }
        return version;
    }

    @Override
    public int compareTo(MSvcVersion o) {
        if (this.mainVersionNum > o.mainVersionNum) {
            return 1;
        } else if (this.mainVersionNum < o.mainVersionNum) {
            return -1;
        } else {
            if (this.childVersionNum > o.childVersionNum) {
                return 1;
            } else if (this.childVersionNum < o.childVersionNum) {
                return -1;
            } else {
                return Integer.compare(this.fixVersionNum, o.fixVersionNum);
            }
        }
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
