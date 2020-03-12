package com.ll.service.utils;


import com.septemberhx.common.service.*;
import com.septemberhx.common.service.diff.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Lei on 2019/12/17 16:37
 * @author Lei
 */
public class GetServiceDiff {
    public static MServiceDiff getDiff(MService mService1, MService mService2) {
        MServiceDiff mServiceDiff = new MServiceDiff();
        List<MDiff> list = new ArrayList<>();
        if (mService1.getServiceVersion().equals(mService2.getServiceVersion())) {
            return null;
        }else{
            list.add(new MDiff(MDiffType.SERVICE_VERSION_DIFF,mService1.getServiceVersion(),mService2.getServiceVersion()));
        }
        if (mService1.getPort() != mService2.getPort()) {
            list.add(new MDiff(MDiffType.SERVICE_PORT_DIFF,mService1.getPort(),mService2.getPort()));
        }
        if (!mService1.getGitUrl().equals(mService2.getGitUrl())) {
            list.add(new MDiff(MDiffType.SERVICE_PATH_DIFF,mService1.getGitUrl(),mService2.getGitUrl()));
        }
        if (!mService1.getServiceName().equals(mService2.getServiceName())) {
            list.add(new MDiff(MDiffType.SERVICE_NAME_DIFF,mService1.getServiceName(),mService2.getServiceName()));
        }
        mServiceDiff.setList(list);
        List<MServiceInterfaceDiff> mServiceInterfaceDiffs = new ArrayList<>();
        List<MSvcInterface> shareversion1 = new ArrayList<>();
        List<MSvcInterface> shareversion2 = new ArrayList<>();
        Set<String> set1 = mService1.getServiceInterfaceMap().keySet();
        Set<String> set2 = mService2.getServiceInterfaceMap().keySet();
        for (String string : set1) {
            if (set2.contains(string)) {
                shareversion1.add(mService1.getServiceInterfaceMap().get(string));
                shareversion2.add(mService2.getServiceInterfaceMap().get(string));
                set2.remove(string);
            } else {
                // verison1 独有
                MServiceInterfaceNumDiff mServiceInterfaceNumDiff = new MServiceInterfaceNumDiff(MDiffInterface.INTERFACE_REDUCE);
                mServiceInterfaceNumDiff.setMSvcInterface(mService1.getServiceInterfaceMap().get(string));
                mServiceInterfaceDiffs.add(mServiceInterfaceNumDiff);
            }
        }
        // version2 独有
        for (String string : set2) {
            MServiceInterfaceNumDiff mServiceInterfaceNumDiff = new MServiceInterfaceNumDiff(MDiffInterface.INTERFACE_ADD);
            mServiceInterfaceNumDiff.setMSvcInterface(mService2.getServiceInterfaceMap().get(string));
            mServiceInterfaceDiffs.add(mServiceInterfaceNumDiff);
        }
        set1.clear();
        set2.clear();
        List<MServiceInterfaceDiff> m = getInterfaceDiff(shareversion1, shareversion2);
        mServiceInterfaceDiffs.addAll(m);
        mServiceDiff.setMServiceInterfaceDiffs(mServiceInterfaceDiffs);
        return mServiceDiff;
    }

    public static List<MServiceInterfaceDiff> getInterfaceDiff(List<MSvcInterface> shareversion1, List<MSvcInterface> shareversion2) {
        List<MServiceInterfaceDiff> list = new ArrayList<>();
        int size = shareversion1.size();
        for (int i = 0; i < size; i++) {
            MServiceInterfaceChangeDiff mServiceInterfaceChangeDiff = new MServiceInterfaceChangeDiff(MDiffInterface.INTERFACE_CHANGE);
            mServiceInterfaceChangeDiff.setPathUrl(shareversion1.get(i).getPatternUrl());
            List<MDiff> mDiffs = new ArrayList<>();
            if (!shareversion1.get(i).getFunctionName().equals(shareversion2.get(i).getFunctionName())) {
                mDiffs.add(new MDiff(MDiffType.INTERFACE_METHEDNAME_DIFF,shareversion1.get(i).getFunctionName(),shareversion2.get(i).getFunctionName()));
            }
            if (!shareversion1.get(i).getRequestMethod().equals(shareversion2.get(i).getRequestMethod())) {
                mDiffs.add(new MDiff(MDiffType.INTERFACE_REQUESTMETHOD_DIFF,shareversion1.get(i).getRequestMethod(),shareversion2.get(i).getRequestMethod()));
            }
            if (!shareversion1.get(i).getReturnType().equals(shareversion2.get(i).getReturnType())) {
                mDiffs.add(new MDiff(MDiffType.INTERFACE_RETURNTYPE_DIFF,shareversion1.get(i).getReturnType(),shareversion2.get(i).getReturnType()));
            }
            if (!shareversion1.get(i).getFuncDescription().getFunc().equals(shareversion2.get(i).getFuncDescription().getFunc())) {
                mDiffs.add(new MDiff(MDiffType.INTERFACE_FUNCTION_FEATURE_DIFF,shareversion1.get(i).getFuncDescription().getFunc(),shareversion2.get(i).getFuncDescription().getFunc()));
            }
            if (shareversion1.get(i).getFuncDescription().getSla() != shareversion2.get(i).getFuncDescription().getSla()) {
                mDiffs.add(new MDiff(MDiffType.INTERFACE_FUNCTION_LEVEL_DIFF,shareversion1.get(i).getFuncDescription().getSla(),shareversion2.get(i).getFuncDescription().getSla()));
            }
            mServiceInterfaceChangeDiff.setList(mDiffs);
            List<MParamerDiff> paramerDiffs = getParamerDiff(shareversion1.get(i), shareversion2.get(i));
            mServiceInterfaceChangeDiff.setParamerDiffs(paramerDiffs);
            if(!mDiffs.isEmpty() || !paramerDiffs.isEmpty()){
                list.add(mServiceInterfaceChangeDiff);
            }
        }
        if(list.isEmpty()){
            return null;
        }
        return list;
    }

    public static List<MParamerDiff> getParamerDiff(MSvcInterface mSvcInterface1, MSvcInterface mSvcInterface2) {
        List<MParamerDiff> list = new ArrayList<>();
        List<MParamer> interfaceversion1 = mSvcInterface1.getParams();
        List<MParamer> interfaceversion2 = mSvcInterface2.getParams();
        List<MParamer> all1 = new ArrayList<>();
        List<MParamer> all2 = new ArrayList<>();
        for (int i = 0; i < interfaceversion1.size(); i++) {
            for (int j = 0; j < interfaceversion2.size(); j++) {
                if (interfaceversion1.get(i).getName().equals(interfaceversion2.get(j).getName())) {
                    all1.add(interfaceversion1.get(i));
                    all2.add(interfaceversion2.get(j));
                }
            }
        }
        interfaceversion1.removeAll(all1);
        interfaceversion2.removeAll(all2);
        for (int i = 0; i < all1.size(); i++) {
            MParamerChangeDiff mParamerChangeDiff = new MParamerChangeDiff(MDiffParamer.PARAMER_CHANGE);
            List<MDiff> mDiffs = new ArrayList<>();
            if (!all1.get(i).toString().equals(all2.get(i).toString())) {
                mParamerChangeDiff.setParamerName(all1.get(i).getName());
                if (!all1.get(i).getRequestname().equals(all2.get(i).getRequestname())) {
                    mDiffs.add(new MDiff(MDiffType.PARAMER_REQUESTNAME_DIFF,all1.get(i).getRequestname(),all2.get(i).getRequestname()));
                }
                if (!all1.get(i).getType().equals(all2.get(i).getType())) {
                    mDiffs.add(new MDiff(MDiffType.PARAMER_TYPE_DIFF,all1.get(i).getType(),all2.get(i).getType()));
                }
                if (!all1.get(i).getMethod().equals(all2.get(i).getMethod())) {
                    mDiffs.add(new MDiff(MDiffType.PARAMER_REQUESTMETHOD_DIFF,all1.get(i).getRequestname(),all2.get(i).getRequestname()));
                }
                if (!all1.get(i).getDefaultObject().equals(all2.get(i).getDefaultObject())) {
                    mDiffs.add(new MDiff(MDiffType.PARAMER_DEFAULT_DIFF,all1.get(i).getDefaultObject(),all2.get(i).getDefaultObject()));
                }
            }
            list.add(mParamerChangeDiff);
        }
        for (int i = 0; i < interfaceversion1.size(); i++) {
            MParamerNumDiff mParamerNumDiff = new MParamerNumDiff(MDiffParamer.PARAMER_REDUCE);
            mParamerNumDiff.setMParamer(interfaceversion1.get(i));
            list.add(mParamerNumDiff);
        }
        for (int i = 0; i < interfaceversion2.size(); i++) {
            MParamerNumDiff mParamerNumDiff = new MParamerNumDiff(MDiffParamer.PARAMER_ADD);
            mParamerNumDiff.setMParamer(interfaceversion2.get(i));
            list.add(mParamerNumDiff);
        }
        return list;
    }
}
