package com.ll.zuulserver.routing;


import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Lei on 2019/12/18 15:42
 */


public class MGetExample {

    public static List<ServiceInstance> getExammpleByVersion(DiscoveryClient discoveryClient,  String version, String servicename ){
        List<ServiceInstance> versions = new ArrayList<>();
        List<ServiceInstance> list = discoveryClient.getInstances(servicename);
        for(ServiceInstance serviceInstance:list){
            Map<String,String> metadata = serviceInstance.getMetadata();
            String this_version = metadata.get("version");
            if(this_version.equals(version)){
                versions.add(serviceInstance);
            }
        }
        return  versions;
    }

    public static List<ServiceInstance> getExammpleByFunction(){
        return null;
    }
}
