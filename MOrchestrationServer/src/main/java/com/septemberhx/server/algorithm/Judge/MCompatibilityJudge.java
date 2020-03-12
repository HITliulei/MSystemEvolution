package com.septemberhx.server.algorithm.Judge;

import com.septemberhx.common.service.diff.*;

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
        if(list.isEmpty()){
            // There is no difference except for internal implementation
            return true;
        }
        for(MServiceInterfaceDiff mServiceInterfaceDiff: list){
            if(mServiceInterfaceDiff.getMDiffInterface().equals(MDiffInterface.INTERFACE_CHANGE)){
                MServiceInterfaceChangeDiff mServiceInterfaceChangeDiff = (MServiceInterfaceChangeDiff)mServiceInterfaceDiff;
                if(mServiceInterfaceChangeDiff.getParamerDiffs() != null && !mServiceInterfaceChangeDiff.getParamerDiffs().isEmpty()){
                    return false;
                }
                List<MDiff> mDiffs = mServiceDiff.getList();
                if (!mDiffs.isEmpty()){
                    for (MDiff mDiff: mDiffs){
                        if(mDiff.getType().equals(MDiffType.INTERFACE_REQUESTMETHOD_DIFF) || mDiff.getType().equals(MDiffType.INTERFACE_FUNCTION_FEATURE_DIFF)){
                            return false;
                        }
                    }
                }
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
