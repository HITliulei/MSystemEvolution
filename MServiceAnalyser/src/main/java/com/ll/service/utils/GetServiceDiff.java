package com.ll.service.utils;

import com.septemberhx.common.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Lei on 2019/12/17 16:37
 */
public class GetServiceDiff {


    public static MServiceDiff getDiff(MService mService1, MService mService2){
        if(mService1.getServiceVersion().equals(mService2.getServiceVersion())){
            return null;
        }
        MServiceDiff mServiceDiff = new MServiceDiff();
        if(mService1.getPort() == mService2.getPort()){
            mServiceDiff.setPortChange("no change");
        }else{
            mServiceDiff.setPortChange(mService1.getPort() +"——>"+ mService2.getPort());
        }
        if(mService1.getGitUrl().equals(mService2.getGitUrl())){
            mServiceDiff.setUrlChange("no change");
        }else{
            mServiceDiff.setUrlChange(mService1.getGitUrl() +"——>"+ mService2.getGitUrl());
        }
        if(mService1.getServiceName().equals(mService2.getServiceName())){
            mServiceDiff.setServiceNameChange("no change");
        }else{
            mServiceDiff.setServiceNameChange(mService1.getServiceName() +"——>"+ mService2.getServiceName());
        }
        if(mService1.getServiceVersion().getMainVersionNum() != mService2.getServiceVersion().getMainVersionNum()){
            mServiceDiff.setVersionChange("main version change");
        }else{
            if(mService1.getServiceVersion().getChildVersionNum() != mService2.getServiceVersion().getChildVersionNum()){
                mServiceDiff.setVersionChange("child version change");
            }else{
                mServiceDiff.setVersionChange("fixed version change");
            }
        }
        List<MServiceInterface> addInterface = new ArrayList<>();
        List<MServiceInterface> reduceInterface = new ArrayList<>();
        List<MServiceInterface> share_version1 = new ArrayList<>();
        List<MServiceInterface> share_version2 = new ArrayList<>();
        Set<String> set1 = mService1.getServiceInterfaceMap().keySet();
        Set<String> set2 = mService2.getServiceInterfaceMap().keySet();
        for(String string:set1){
            if(set2.contains(string)){
                share_version1.add(mService1.getServiceInterfaceMap().get(string));
                share_version2.add(mService2.getServiceInterfaceMap().get(string));
                set2.remove(string);
            }else{  // verison1 独有
                reduceInterface.add(mService1.getServiceInterfaceMap().get(string));
            }
        }
        for(String string: set2){  // version2 独有
            addInterface.add(mService2.getServiceInterfaceMap().get(string));
        }
        set1.clear();
        set2.clear();
        mServiceDiff.setAddInterface(addInterface);
        mServiceDiff.setReduceInterface(reduceInterface);
        List<MServiceInterfaceDiff> m =getInterfaceDiff(share_version1,share_version2);
        mServiceDiff.setChangeInterface(m);
        return mServiceDiff;

    }

    public static List<MServiceInterfaceDiff> getInterfaceDiff(List<MServiceInterface> share_version1, List<MServiceInterface> share_version2){
        List<MServiceInterfaceDiff> list = new ArrayList<>();
        int size = share_version1.size();
        for(int i = 0 ;i<size;i++){
            MServiceInterfaceDiff mServiceInterfaceDiff = new MServiceInterfaceDiff();
            mServiceInterfaceDiff.setPathurl(share_version1.get(i).getPatternUrl());
            if(share_version1.get(i).getFunctionName().equals(share_version2.get(i).getFunctionName())){
                mServiceInterfaceDiff.setMethodnameChange("no change");
            }else{
                mServiceInterfaceDiff.setMethodnameChange(share_version1.get(i).getFunctionName()+"——>"+share_version2.get(i).getFunctionName());
            }
            if(share_version1.get(i).getRequestMethod().equals(share_version2.get(i).getRequestMethod())){
                mServiceInterfaceDiff.setRequestMethodChange("no change");
            }else{
                mServiceInterfaceDiff.setRequestMethodChange(share_version1.get(i).getRequestMethod()+"——>"+share_version2.get(i).getRequestMethod());
            }
            if(share_version1.get(i).getReturnType().equals(share_version2.get(i).getReturnType())){
                mServiceInterfaceDiff.setReturnTypeChange("no change");
            }else{
                mServiceInterfaceDiff.setReturnTypeChange(share_version1.get(i).getReturnType()+"——>"+share_version2.get(i).getReturnType());
            }
            if(share_version1.get(i).getFuncDescription().getFeatureName().equals(share_version2.get(i).getFuncDescription().getFeatureName())){
                mServiceInterfaceDiff.setFunctionDiscribe("no change");
            }else{
                mServiceInterfaceDiff.setFunctionDiscribe(share_version1.get(i).getFuncDescription().getFeatureName()+"——>"+share_version2.get(i).getFuncDescription().getFeatureName());
            }
            if(share_version1.get(i).getFuncDescription().getSlaLevel() == share_version2.get(i).getFuncDescription().getSlaLevel()){
                mServiceInterfaceDiff.setSlaLevelDiff("no change");
            }else{
                mServiceInterfaceDiff.setFunctionDiscribe(share_version1.get(i).getFuncDescription().getSlaLevel()+"——>"+share_version2.get(i).getFuncDescription().getSlaLevel());
            }
            List<MParamerDiff> paramerDiffs = getParamerDiff(share_version1.get(i),share_version2.get(i));
            mServiceInterfaceDiff.setParameChanges(paramerDiffs);
            list.add(mServiceInterfaceDiff);
        }
        return list;

    }

    public static List<MParamerDiff> getParamerDiff(MServiceInterface mServiceInterface1, MServiceInterface mServiceInterface2){
        List<MParamerDiff> list = new ArrayList<>();
        List<MParamer> interface_version1 = mServiceInterface1.getParams();
        List<MParamer> interface_version2 = mServiceInterface2.getParams();
        List<MParamer> all_1 = new ArrayList<>();
        List<MParamer> all_2 = new ArrayList<>();
        for(int i = 0;i<interface_version1.size();i++){
            for(int j = 0;j<interface_version2.size();j++){
                if(interface_version1.get(i).getName().equals(interface_version2.get(j).getName())){
                    all_1.add(interface_version1.get(i));
                    all_2.add(interface_version2.get(j));
                }
            }
        }
        interface_version1.removeAll(all_1);
        interface_version2.removeAll(all_2);
        for(int i = 0;i<all_1.size();i++){
            MParamerDiff paramerDiff = new MParamerDiff();
            if(all_1.get(i).toString().equals(all_2.get(i).toString())){
                paramerDiff.setType("no change");
                paramerDiff.setName(all_1.get(i).getName());
                paramerDiff.setRequestname(all_1.get(i).getRequestname());
                paramerDiff.setDataType(all_1.get(i).getType());
                paramerDiff.setRequestmethod(all_1.get(i).getMethod());
                paramerDiff.setDefaultvalue(all_1.get(i).getDefaultObject());
            }else{
                paramerDiff.setType("change");
                paramerDiff.setName(all_1.get(i).getName());
                if(all_1.get(i).getRequestname().equals(all_2.get(i).getRequestname())){
                    paramerDiff.setRequestname("no change ——>"+ all_1.get(i).getRequestname());
                }else{
                    paramerDiff.setRequestname(all_1.get(i).getRequestname() + "    ——>    " + all_2.get(i).getRequestname());
                }
                if(all_1.get(i).getType().equals(all_2.get(i).getType())){
                    paramerDiff.setType("no change ——>" + all_1.get(i).getType());
                }else{
                    paramerDiff.setType(all_1.get(i).getType() + "    ——>    " + all_2.get(i).getType());
                }
                if(all_1.get(i).getMethod().equals(all_2.get(i).getMethod())){
                    paramerDiff.setRequestmethod("no change ——>" + all_1.get(i).getMethod());
                }else{
                    paramerDiff.setRequestmethod(all_1.get(i).getMethod() + "    ——>    " + all_2.get(i).getMethod());
                }
                if(all_1.get(i).getDefaultObject().equals(all_2.get(i).getDefaultObject())){
                    paramerDiff.setDefaultvalue("no change ——>" + all_1.get(i).getDefaultObject());
                }else{
                    paramerDiff.setDefaultvalue(all_1.get(i).getDefaultObject() + "    ——>    " + all_2.get(i).getDefaultObject());
                }
            }
            list.add(paramerDiff);
        }
        for(int i =0;i<interface_version1.size();i++){
            MParamerDiff paramerDiff = new MParamerDiff();
            paramerDiff.setType("减少");
            paramerDiff.setName(interface_version1.get(i).getName());
            paramerDiff.setRequestmethod(interface_version1.get(i).getMethod());
            paramerDiff.setDefaultvalue(interface_version1.get(i).getDefaultObject());
            paramerDiff.setRequestname(interface_version1.get(i).getRequestname());
            paramerDiff.setDataType(interface_version1.get(i).getType());
            list.add(paramerDiff);
        }
        for(int i =0;i<interface_version2.size();i++){
            MParamerDiff paramerDiff = new MParamerDiff();
            paramerDiff.setType("增加");
            paramerDiff.setName(interface_version2.get(i).getName());
            paramerDiff.setRequestmethod(interface_version2.get(i).getMethod());
            paramerDiff.setDefaultvalue(interface_version2.get(i).getDefaultObject());
            paramerDiff.setRequestname(interface_version2.get(i).getRequestname());
            paramerDiff.setDataType(interface_version2.get(i).getType());
            list.add(paramerDiff);
        }
        return list;
    }
}
