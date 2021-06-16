package com.septemberhx.server.client;

import com.netflix.appinfo.InstanceInfo;
import com.septemberhx.common.bean.agent.MDeployPodRequest;
import com.septemberhx.common.bean.agent.MInstanceInfoBean;
import com.septemberhx.common.bean.agent.MInstanceInfoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @Author Lei
 * @Date 2020/3/16 14:56
 * @Version 1.0
 */

@Component
public class ConnectToClient {

    private RestTemplate restTemplate = new RestTemplate();

    private String clusterIp = "18.166.22.170";

    private String clusterPort = "46832";


    public List<ServiceInstance> lookup(String serviceId){
        List<ServiceInstance> list = new ArrayList<>();
        return  Arrays.asList(restTemplate.getForObject("http://"+clusterIp+":"+clusterPort+"/lookup/"+serviceId,ServiceInstance[].class));
    }

    public List<InstanceInfo> getallinstance(){
        return  Arrays.asList(restTemplate.getForObject("http://"+clusterIp+":"+clusterPort+"/instanceInfoList1",InstanceInfo[].class));
    }
    public List<MInstanceInfoBean> getAllMintancesInfo(){
        MInstanceInfoResponse mInstanceInfoResponse = restTemplate.getForObject("http://"+clusterIp+":"+clusterPort+"/instanceInfoList",MInstanceInfoResponse.class);
        return mInstanceInfoResponse.getInfoBeanList();
    }


    public void deploy(MDeployPodRequest mDeployPodRequest){
        HttpHeaders requestHeaders = new HttpHeaders();
        HttpEntity<Object> paramers = new HttpEntity<>(mDeployPodRequest,requestHeaders);
        restTemplate.postForLocation("http://"+clusterIp+":"+clusterPort+"/magent/deploy",paramers);
    }

    public void register(MDeployPodRequest mDeployPodRequest){
        HttpHeaders requestHeaders = new HttpHeaders();
        HttpEntity<Object> paramers = new HttpEntity<>(mDeployPodRequest,requestHeaders);
        restTemplate.postForLocation("http://"+clusterIp+":"+clusterPort+"/magent/register",paramers);
    }

    public void deleteInstance(String dockerInstanceId){
        restTemplate.delete("http://"+clusterIp+":"+clusterPort+"/magent/deleteInstance?dockerInstanceId="+dockerInstanceId);
    }


    public Map<String,String> getAllNodeLabel(){
        return restTemplate.getForObject("http://"+clusterIp+":"+clusterPort+"/magent/getAllNodeLable",Map.class);
    }
}
