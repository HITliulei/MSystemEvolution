package com.septemberhx.agent.controller;

import com.netflix.appinfo.InstanceInfo;
import com.septemberhx.agent.utils.MClientUtils;
import com.septemberhx.common.bean.agent.MInstanceInfoBean;
import com.septemberhx.common.bean.agent.MInstanceInfoResponse;
import com.septemberhx.common.service.MSvcVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RegistryLookUp {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private MClientUtils clientUtils;

    @GetMapping(path = "/lookup/{serviceId}")
    public List<ServiceInstance> lookup(@PathVariable String serviceId) {
        return discoveryClient.getInstances(serviceId);
    }

    @RequestMapping(path = "/instanceInfoList", method = RequestMethod.GET)
    public MInstanceInfoResponse getInstanceInfoList() {
        MInstanceInfoResponse response = new MInstanceInfoResponse();
        response.setInfoBeanList(this.clientUtils.getInstanceInfoList());
        return response;
    }
    @RequestMapping(path = "/instanceInfoListByName",method = RequestMethod.GET)
    public MInstanceInfoResponse getInstanceInfoListByName(@RequestParam("serviceName")String serviceName){
        MInstanceInfoResponse response = new MInstanceInfoResponse();
        List<MInstanceInfoBean> mInstanceInfoBeans = this.clientUtils.getInstanceInfoList();
        List<MInstanceInfoBean> result = new ArrayList<>();
        for ( MInstanceInfoBean mInstanceInfoBean : mInstanceInfoBeans){
            if (mInstanceInfoBean.getServiceName().equalsIgnoreCase(serviceName)){
                result.add(mInstanceInfoBean);
            }
        }
        response.setInfoBeanList(result);
        System.out.println("返回的微服务实例列表为" + result);
        return  response;
    }

    @RequestMapping(path = "/instanceInfoListByNameAndVersion",method = RequestMethod.GET)
    public MInstanceInfoResponse instanceInfoListByNameAndVersion(@RequestParam("serviceId")String serviceId){
        String[] a = serviceId.split("_");
        String serviceName = a[0];
        MSvcVersion serviceVersion = MSvcVersion.fromStr(a[1]);
        MInstanceInfoResponse response = new MInstanceInfoResponse();
        List<MInstanceInfoBean> mInstanceInfoBeans = this.clientUtils.getInstanceInfoList();
        List<MInstanceInfoBean> result = new ArrayList<>();
        for ( MInstanceInfoBean mInstanceInfoBean : mInstanceInfoBeans){
            if (mInstanceInfoBean.getServiceName().equalsIgnoreCase(serviceName) && serviceVersion.equals(MSvcVersion.fromStr(mInstanceInfoBean.getServiceVersion()))){
                result.add(mInstanceInfoBean);
            }
        }
        response.setInfoBeanList(result);
        return  response;
    }



    @GetMapping(path = "/nameAndVersion/{serviceId}")
    public List<ServiceInstance> nameAndVersion(@PathVariable String serviceId){
        String[] a = serviceId.split("_");
        String serviceName = a[0];
        String serviceVersion = MSvcVersion.fromStr(a[1]).toString();
        List<ServiceInstance> list = discoveryClient.getInstances(serviceName);
        List<ServiceInstance> result = new ArrayList<>();
        for(ServiceInstance serviceInstance: list){
            String instanceVersion = serviceInstance.getMetadata().get("version");
            if(MSvcVersion.fromStr(instanceVersion).toString().equals(serviceVersion)){
                result.add(serviceInstance);
            }
        }
        return result;
    }

    @RequestMapping(path = "/instanceInfoList1",method = RequestMethod.GET)
    public List<InstanceInfo> getInstanceInfoList1() {
        return this.clientUtils.getInstanceInfoList1();
    }

}