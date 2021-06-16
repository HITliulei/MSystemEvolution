package com.septemberhx.server.dao;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author Lei
 * @Date 2020/3/14 20:50
 * @Version 1.0
 */

@Getter
@Setter
@ToString
public class MDependencyDao{
    private String dependencyName;
    private String dependencyId;
    private String serviceId;
    private String serviceDenpendencyName;
    private String serviceDependencyInterfaceName;
    private String serviceDenpendencyVersion;
    private String functionDescribe;
    private Integer functionLevel;

    public MDependencyDao(){

    }
    public MDependencyDao(String dependencyName, String dependencyId,
                          String serviceId, String serviceDenpendencyName,
                          String serviceDependencyInterfaceName, String serviceDenpendencyVersion,
                          String functionDescribe, Integer functionLevel) {
        this.dependencyName = dependencyName;
        this.dependencyId = dependencyId;
        this.serviceId = serviceId;
        this.serviceDenpendencyName = serviceDenpendencyName;
        this.serviceDependencyInterfaceName = serviceDependencyInterfaceName;
        this.serviceDenpendencyVersion = serviceDenpendencyVersion;
        this.functionDescribe = functionDescribe;
        this.functionLevel = functionLevel;
    }


    public static Set<String> getVersions(String st){
        if(st == null){
            return null;
        }
        String[] versions = st.split(",");
        Set<String> set = new HashSet<>();
        for(String string: versions){
            set.add(string);
        }
        return set;
    }

    public static String getVersion(Set<String> versions){
        if(versions == null){
            return null;
        }
        StringBuilder a = new StringBuilder();
        for(String s: versions){
            a.append(s);
            a.append(",");
        }
        a.deleteCharAt(a.lastIndexOf(","));
        return a.toString();
    }

}