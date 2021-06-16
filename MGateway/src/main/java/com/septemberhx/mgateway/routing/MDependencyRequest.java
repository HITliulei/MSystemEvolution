package com.septemberhx.mgateway.routing;

import com.septemberhx.common.bean.agent.MInstanceInfoBean;
import com.septemberhx.common.bean.gateway.MRequestUrl;
import com.septemberhx.common.bean.instance.MDeployVersion;
import com.septemberhx.common.service.MService;
import com.septemberhx.mgateway.client.ConnectToCenter;
import com.septemberhx.mgateway.client.ConnectToClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @Author Lei
 * @Date 2020/3/24 14:14
 * @Version 1.0
 */

public class MDependencyRequest {

    public MRequestUrl getInstanceInfoBean(String name, String id, String hostIp, String serviceName, String serviceVersion, ConnectToClient connectToClient, ConnectToCenter connectToCenter){
        if(serviceName!=null){
            String node = connectToCenter.getNode(hostIp, serviceName+"_"+serviceVersion);
            if(node == null || node.equals("")){

            }
            List<MInstanceInfoBean> localResult = new ArrayList<>();
            List<MInstanceInfoBean> results = new ArrayList<>();
            Map<String, List<MService>> map = connectToCenter.getServiceByDependenvyNameAndIdAndServiceId(name, id, serviceName+"_"+serviceVersion);
            for(String interfaceName : map.keySet()){
                List<MService> mServices = map.get(interfaceName);
                for(MService mService:mServices){
                    List<MInstanceInfoBean> mInstanceInfoBeans = connectToClient.instanceInfoListByNameAndVersion(mService.getServiceName()+"_"+mService.getMSvcDepDesc().toString()).getInfoBeanList();
                    for(MInstanceInfoBean mInstanceInfoBean: mInstanceInfoBeans){
                        if (mInstanceInfoBean.getDockerInfo().getNodeLabel().equals(node)){
                            localResult.add(mInstanceInfoBean);
                        }else{
                            results.add(mInstanceInfoBean);
                        }
                    }
                }
                if(!localResult.isEmpty()){
                    int a = new Random().nextInt(localResult.size());
                    MInstanceInfoBean mInstanceInfoBean = localResult.get(a);
                    connectToCenter.updateUseful(mInstanceInfoBean);
                    return new MRequestUrl(mInstanceInfoBean.getIp(), mInstanceInfoBean.getPort().toString(), interfaceName);
                }else{
                    if(results.isEmpty()){
                        MService mService = getHightestMService(mServices);
                        connectToCenter.deployOneInstance(new MDeployVersion(mService.getServiceName(), mService.getServiceVersion().toString(), node));
                        return null;
                    }else{
                        int a = new Random().nextInt(results.size());
                        MInstanceInfoBean mInstanceInfoBean = results.get(a);
                        connectToCenter.updateUseful(mInstanceInfoBean);
                        return new MRequestUrl(mInstanceInfoBean.getIp(), mInstanceInfoBean.getPort().toString(), interfaceName);
                    }
                }
            }
            return null;
        }else{
            List<MInstanceInfoBean> result = new ArrayList<>();
            Map<String, List<MService>> map = connectToCenter.getServiceByDependenvyNameAndId(name, id);
            for(String interfaceName: map.keySet()){
                List<MService> mServices = map.get(interfaceName);
                for(MService mService: mServices){
                    String serviceName1 = mService.getServiceName();
                    String serviceVersion1 = mService.getServiceVersion().toString();
                    List<MInstanceInfoBean> list  = connectToClient.instanceInfoListByNameAndVersion(serviceName1+"_"+serviceVersion1).getInfoBeanList();
                    result.addAll(list);
                }
                if(result.isEmpty()){
                    MService mService = getHightestMService(mServices);
                    List<String> nodes = connectToClient.getAllNode();
                    connectToCenter.deployOneInstance(new MDeployVersion(mService.getServiceName(), mService.getServiceVersion().toString(), nodes.get(new Random().nextInt(nodes.size()))));
                    return null;
                }else{
                    int a = new Random().nextInt(result.size());
                    MInstanceInfoBean mInstanceInfoBean = result.get(a);
                    connectToCenter.updateUseful(mInstanceInfoBean);
                    return new MRequestUrl(mInstanceInfoBean.getIp(), mInstanceInfoBean.getPort().toString(), interfaceName);
                }
            }
            return null;
        }
    }


    public MService getHightestMService(List<MService> list){
        String serviceName = list.get(0).getServiceName();
        MService mService = new MService();
        String version = "0.0.0";
        for(MService mService1:list){
            if(!serviceName.equals(mService1.getServiceName())){
                break;
            }
            if(Integer.parseInt(mService1.getServiceVersion().toString().replaceAll("\\.","")) >= Integer.parseInt(version.replaceAll("\\.",""))){
                version = mService1.getServiceVersion().toString();
                mService = mService1;
            }
        }
        return mService;
    }
}
