package com.septemberhx.mgateway.routing;

import com.septemberhx.common.bean.agent.MInstanceInfoBean;
import com.septemberhx.common.bean.instance.MDeployVersion;
import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.MSvcVersion;
import com.septemberhx.common.service.dependency.BaseSvcDependency;
import com.septemberhx.mgateway.client.ConnectToCenter;
import com.septemberhx.mgateway.client.ConnectToClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by Lei on 2019/12/18 15:42
 */
@Component
public class MPathRquest {


    private static Logger logger = LogManager.getLogger(MPathRquest.class);

    public List<MInstanceInfoBean> getExammpleByVersion(ConnectToCenter connectToCenter,ConnectToClient connectToClient,String serviceId, String requestVersion, String requestname, String hostIp ){
        String node = connectToCenter.getNode(hostIp, serviceId);
        List<MInstanceInfoBean> result = new ArrayList<>();
        List<MInstanceInfoBean> localResult = new ArrayList<>();
        String requestServiceId = requestname + "_" + requestVersion;
        List<MInstanceInfoBean> mInstanceInfoBeans = connectToClient.instanceInfoListByNameAndVersion(requestServiceId).getInfoBeanList();
        if(!mInstanceInfoBeans.isEmpty()){
            for(MInstanceInfoBean mInstanceInfoBean: mInstanceInfoBeans){
                String dependencyNode =mInstanceInfoBean.getDockerInfo().getNodeLabel();
                if(dependencyNode.equals(node)){
                    localResult.add(mInstanceInfoBean);
                }else{
                    result.add(mInstanceInfoBean);
                }
            }
        }
        if(localResult.isEmpty()){
            if(result.isEmpty()){
                System.out.println("没有满足的实例，进行部署");
                List<String> nodeList = new ConnectToClient().getAllNode();
                MDeployVersion mDeployVersion = new MDeployVersion(requestname, requestVersion, nodeList.get(new Random().nextInt(nodeList.size())));
                connectToCenter.deployOneInstance(mDeployVersion);
                System.out.println("部署成功，可以在再次进行部署");
                return null;
            }else{
                return result;
            }
        }
        return  localResult;
    }

    public List<MInstanceInfoBean> getExammpleByWithoutVersion(ConnectToCenter connectToCenter,ConnectToClient connectToClient, String serviceName, String serviceVersion, String requestURI , String hostIp){
        String node = connectToCenter.getNode(hostIp, serviceName+"_"+serviceVersion);
        String requstName =  requestURI.split("/")[1];
        System.out.println("没有版本请求的微服务名称为" + requstName);
        String request = requestURI.replaceFirst("/"+requstName,"");
        if(request.contains("\\?")){
            request = request.split("\\?")[0];
        }
        Set<MSvcVersion> versionsFor = new HashSet<>();
        if(serviceVersion != null && serviceName!= null){
            MService mService = connectToCenter.getServiceByNameAndVersion(serviceName,serviceVersion);
            Map<String,Map<String, BaseSvcDependency>> map = mService.getMSvcDepDesc().getDependencyMaps();
            for(String dependencyName:map.keySet()){
                Map<String, BaseSvcDependency> baseSvcDependencyMap = map.get(dependencyName);
                for(String dependencyId:baseSvcDependencyMap.keySet()){
                    BaseSvcDependency baseSvcDependency = baseSvcDependencyMap.get(dependencyId);
                    if(baseSvcDependency.getServiceName()!=null
                            && requstName.equalsIgnoreCase(baseSvcDependency.getServiceName())
                            && request.contains(baseSvcDependency.getPatternUrl())){
                        if(baseSvcDependency.getVersionSet()!=null){
                            versionsFor.addAll(baseSvcDependency.getVersionSet());
                        }else{
                            versionsFor.addAll(getVersionByPath(connectToCenter, requstName, request));
                        }
                    }
                }
            }
        }else{  //客户端请求 寻找可实行的版本
            versionsFor.addAll(getVersionByPath(connectToCenter, requstName,request));
        }
        Set<String> versions = new HashSet<>();
        for(MSvcVersion mSvcVersion:versionsFor){
            versions.add(mSvcVersion.toString());
        }
        System.out.println("可行的版本集合为" + versions);
        List<MInstanceInfoBean> result = new ArrayList<>();
        List<MInstanceInfoBean> localResult = new ArrayList<>();
        List<MInstanceInfoBean> mInstanceInfoBeans = connectToClient.getListInstanceInfoByName(requstName).getInfoBeanList();
        if(!mInstanceInfoBeans.isEmpty()){
            for(MInstanceInfoBean mInstanceInfoBean:mInstanceInfoBeans){
                if(versions.contains(MSvcVersion.fromStr(mInstanceInfoBean.getServiceVersion()).toString())){
                    String requestNode = mInstanceInfoBean.getDockerInfo().getNodeLabel();
                    if(requestNode.equals(node)){
                        localResult.add(mInstanceInfoBean);
                    }else{
                        result.add(mInstanceInfoBean);
                    }
                }
            }
        }
        if(localResult.isEmpty()){
            if(result.isEmpty()){
                System.out.println("没有满足的实例，进行部署");
                List<String> nodeList = new ConnectToClient().getAllNode();
                MDeployVersion mDeployVersion = new MDeployVersion(requstName, versions.toArray()[0].toString(), nodeList.get(new Random().nextInt(nodeList.size())));
                connectToCenter.deployOneInstance(mDeployVersion);
                System.out.println("部署成功，可以在再次进行部署");
                return null;
            }
            return result;
        }
        return localResult;
    }

    public Set<MSvcVersion> getVersionByPath(ConnectToCenter connectToCenter,String requestName, String request){
        System.out.println("寻找可行的版本" + request);
        Set<MSvcVersion> set = new HashSet<>();
        List<MService> list = connectToCenter.getServiceByName(requestName);
        for(MService mService :list){
            if(mService.getServiceName().equalsIgnoreCase(requestName)){
                for(String string: mService.getServiceInterfaceMap().keySet()){
                    System.out.println("测试的接口为" + string);
                    if(request.contains(string)){
                        set.add(mService.getServiceVersion());
                        break;
                    }
                }
            }
        }
        return set;
    }
}
