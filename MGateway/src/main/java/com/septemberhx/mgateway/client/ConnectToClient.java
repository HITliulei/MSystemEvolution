package com.septemberhx.mgateway.client;

import com.septemberhx.common.bean.agent.MInstanceInfoBean;
import com.septemberhx.common.bean.agent.MInstanceInfoResponse;
import com.septemberhx.common.service.MSvcVersion;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author Lei
 * @Date 2020/5/30 19:32
 * @Version 1.0
 */
@Component
public class ConnectToClient {


    private String clusterIp = "18.166.22.170";

    private String clusterPort = "46832";

    public List<MInstanceInfoBean> getAll(){
        MInstanceInfoResponse mInstanceInfoResponse = new RestTemplate().getForObject("http://"+clusterIp+":"+clusterPort+"/instanceInfoList",MInstanceInfoResponse.class);
        return mInstanceInfoResponse.getInfoBeanList();
    }

    public MInstanceInfoResponse getListInstanceInfo(){
        MInstanceInfoResponse mInstanceInfoResponse = new MInstanceInfoResponse();
        mInstanceInfoResponse.setInfoBeanList(getAll());
        return mInstanceInfoResponse;
    }

    public MInstanceInfoResponse getListInstanceInfoByName(String serviceName){
        System.out.println("根据名字请求微服务实例" + serviceName);
        MInstanceInfoResponse mInstanceInfoResponse = new MInstanceInfoResponse();
        List<MInstanceInfoBean> list = getAll();
        List<MInstanceInfoBean> result = new ArrayList<>();
        for(MInstanceInfoBean mInstanceInfoBean:list){
            if(mInstanceInfoBean.getServiceName().equalsIgnoreCase(serviceName)){
                result.add(mInstanceInfoBean);
            }
        }
        mInstanceInfoResponse.setInfoBeanList(result);
        return mInstanceInfoResponse;
    }

    public MInstanceInfoResponse instanceInfoListByNameAndVersion(String serviceId){
        System.out.println("根据id请求微服务" + serviceId);
        String[] a = serviceId.split("_");
        String serviceName = a[0];
        MSvcVersion serviceVersion = MSvcVersion.fromStr(a[1]);
        MInstanceInfoResponse mInstanceInfoResponse = new MInstanceInfoResponse();
        List<MInstanceInfoBean> list = getAll();
        List<MInstanceInfoBean> result = new ArrayList<>();
        for ( MInstanceInfoBean mInstanceInfoBean : list){
            if (mInstanceInfoBean.getServiceName().equalsIgnoreCase(serviceName) && serviceVersion.equals(MSvcVersion.fromStr(mInstanceInfoBean.getServiceVersion()))){
                result.add(mInstanceInfoBean);
            }
        }
        mInstanceInfoResponse.setInfoBeanList(result);
        return mInstanceInfoResponse;
    }

    public List<String> getAllNode(){
        Map<String, String> map = new RestTemplate().getForObject("http://"+clusterIp+":"+clusterPort+"/magent/getAllNodeLable", Map.class);
        List<String> list = new ArrayList<>();
        for (String string:map.keySet()){
            list.add(map.get(string));
        }
        return list;
    }




}
