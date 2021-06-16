package com.ll.mv4ms.utils;

import com.netflix.appinfo.ApplicationInfoManager;

import java.util.HashMap;
import java.util.Map;

public class EurekaUtils {

    private static ApplicationInfoManager applicationInfoManager;


    public static void initInfoManager(ApplicationInfoManager a){
        if (applicationInfoManager == null){
            applicationInfoManager = a;
        }
    }

    public static void addKeyValue(String key ,String value){
        Map<String,String> map = new HashMap<>();
        map.put(key,value);
        applicationInfoManager.registerAppMetadata(map);
    }

    public static void addMap(Map<String,String> map){
        applicationInfoManager.registerAppMetadata(map);
    }
}
