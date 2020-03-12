package com.septemberhx.server.algorithm.Judge;

import com.septemberhx.common.service.diff.MDiffInterface;
import com.septemberhx.common.service.diff.MServiceDiff;
import com.septemberhx.common.service.diff.MServiceInterfaceDiff;

import java.util.List;

/**
 * @Author Lei
 * @Date 2020/3/12 15:20
 * @Version 1.0
 */
public class MCompatibilityJudge {

    /**
     * Judge compatibility between high and low versions
     * @param mServiceDiff Differences between microservices
     * @return If the higher version is fully backward compatible with the lower version return true, else return false
     */
    public static boolean mCompatibilityJudge(MServiceDiff mServiceDiff){
        List<MServiceInterfaceDiff> list = mServiceDiff.getMServiceInterfaceDiffs();
        for(MServiceInterfaceDiff mServiceInterfaceDiff: list){
            if(mServiceInterfaceDiff.getMDiffInterface().equals(MDiffInterface.INTERFACE_CHANGE)){
                return false;
            }
            if(mServiceInterfaceDiff.getMDiffInterface().equals(MDiffInterface.INTERFACE_REDUCE)){
                return false;
            }
        }
        return true;
    }
    /**
     * to Judge which version is higher
     * @param version1
     * @param version2
     * @return return true if version1 is higher than version2
     */
    public static boolean ifHightVersion(String version1, String version2){
        if(version1.equalsIgnoreCase(version2)){
            return false;
        }
        Integer v1 = Integer.parseInt(version1.replaceAll("\\.",""));
        Integer v2 = Integer.parseInt(version2.replaceAll("\\.",""));
        if(v1 > v2){
            return true;
        }else{
            return false;
        }
    }
}
