package com.septemberhx.mgateway.client;

import com.septemberhx.common.bean.agent.MInstanceInfoBean;
import com.septemberhx.common.bean.instance.MDeployVersion;
import com.septemberhx.common.service.MService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Author Lei
 * @Date 2020/3/23 22:28
 * @Version 1.0
 */

@Component
public class ConnectToCenter {

    private String ip = "54.65.128.130";

    private String port = "58080";

    @Autowired
    private RestTemplate restTemplate;

    public List<String> getAllServiceName(){
        return Arrays.asList(restTemplate.getForObject("http://"+ip+":"+port+"/getServiceInfo/getAllServiceName", String[].class));
    }

    public List<MService> getServiceByName(String serviceName){
        return Arrays.asList(restTemplate.getForObject("http://"+ip+":"+port+"/getServiceInfo/getServicseByname?serviceName="+serviceName, MService[].class));
    }

    public MService getServiceByNameAndVersion(String serviceName, String serviceVersion){
        return restTemplate.getForObject("http://"+ip+":"+port+"/getServiceInfo/getServiceByNameAndVersion?serviceName="+serviceName+"&serviceVersion="+serviceVersion,MService.class);
    }

    public void updateUseful(MInstanceInfoBean mInstanceInfoBean){
        HttpHeaders requestHeaders = new HttpHeaders();
        HttpEntity<Object> paramers = new HttpEntity<>(mInstanceInfoBean,requestHeaders);
        restTemplate.postForLocation("http://"+ip+":"+port+"/getServiceInfo/updateUseUseinfo",paramers);
    }

    public String getNode(String ipA, String serviceId){
        return restTemplate.getForObject("http://"+ip+":"+port+"/getServiceInfo/getNode?serviceId="+serviceId+"&ip="+ipA,String.class);
    }

    public Map<String, List<MService>> getServiceByDependenvyNameAndId(String name, String id){
        return restTemplate.getForObject("http://"+ip+":"+port+"/getServiceInfo/getMServiceByDependency?name="+name+"&id="+id, Map.class);
    }

    public Map<String, List<MService>> getServiceByDependenvyNameAndIdAndServiceId(String name, String id, String serviceId){
        return restTemplate.getForObject("http://"+ip+":"+port+"/getServiceInfo/getMServiceByDependency?name="+name+"&id="+id+"&serviceId="+serviceId, Map.class);
    }

    public void deployOneInstance(MDeployVersion mDeployVersion){
        HttpHeaders requestHeaders = new HttpHeaders();
        HttpEntity<Object> paramers = new HttpEntity<>(mDeployVersion,requestHeaders);
        restTemplate.postForLocation("http://"+ip+":"+port+"/deploy/deployOneInstanceOnNode",paramers);
    }
}
