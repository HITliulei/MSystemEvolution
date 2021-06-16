package com.septemberhx.server.algorithm.deploy;

import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.MSvcInterface;
import com.septemberhx.common.service.MSvcVersion;
import com.septemberhx.server.client.ConnectToClient;
import com.septemberhx.server.dao.MDependencyDao;
import com.septemberhx.server.dao.MDeployDao;
import com.septemberhx.server.utils.MDatabaseUtils;
import java.util.*;

/**
 * @Author Lei
 * @Date 2020/3/18 12:26
 * @Version 1.0
 */
public class Deletestrategy {

    public static void deleteOneinstance(String serviceName, String serviceVersion, ConnectToClient connectToClient){
        List<MDeployDao> mDeployDaos = MDatabaseUtils.databaseUtils.getDeployByserviceId(serviceName, serviceVersion);
        System.out.println("部署信息" + mDeployDaos);

        // get support serivice and its version
        List<MDependencyDao> mDependencyDaos = MDatabaseUtils.databaseUtils.getSupportByServiceId(serviceName, serviceVersion);
        System.out.println("支持的依赖" + mDependencyDaos);
        // 没有部署过的实例
        if(mDeployDaos.isEmpty()){
            return;
        }
        // no surpoert for orther microservice
        if(mDependencyDaos.isEmpty() || mDependencyDaos==null){
            int a = new Random().nextInt(mDeployDaos.size());
            connectToClient.deleteInstance(mDeployDaos.get(a).getPodId());
            MDatabaseUtils.databaseUtils.deleteDeployById(mDeployDaos.get(a).getPodId());
        }else{
            boolean ifprovide = false;
            // 每个节点上部署的个数
            Map<String,Integer> surpportMap = new HashMap<>();
            Map<String, String> nodelabl = connectToClient.getAllNodeLabel();
            for(String string:nodelabl.keySet()){
                surpportMap.put(nodelabl.get(string),0);
            }
            for(MDependencyDao mDependencyDao : mDependencyDaos){
                List<MDeployDao> list = MDatabaseUtils.databaseUtils.getDeployByTrueserviceId(mDependencyDao.getServiceId());
                System.out.println("得到的部署为");
                if(!list.isEmpty()){
                    ifprovide = true;
                    for(MDeployDao mDeployDao:list){
                        int a = surpportMap.get(mDeployDao.getNodeId());
                        surpportMap.put(mDeployDao.getNodeId(),a+1);
                    }
                }
            }
            System.out.println("支持的个数"+surpportMap);
            if(!ifprovide){
                System.out.println("随机选择");
                int a = new Random().nextInt(mDeployDaos.size());
                connectToClient.deleteInstance(mDeployDaos.get(a).getPodId());
                MDatabaseUtils.databaseUtils.deleteDeployById(mDeployDaos.get(a).getPodId());
            }else if(ifprovide && mDeployDaos.size() > 1){
                System.out.println("有支持的个数并且能够删除");
                // node choose
                Map<String, Integer> itSelfMap = new HashMap<>();
                for(String string:nodelabl.keySet()){
                    itSelfMap.put(nodelabl.get(string),0);
                }
                for(MDeployDao mDeployDao: mDeployDaos){
                    int a = itSelfMap.get(mDeployDao.getNodeId());
                    itSelfMap.put(mDeployDao.getNodeId(), a+1);
                }
                String nodeId = deletOnWhicheNode(surpportMap, itSelfMap);
                System.out.println("选择的节点为"+ nodeId);
                if(nodeId.equalsIgnoreCase("")){
                    return;
                }
                List<MDeployDao> deleteInstancesOnNode = MDatabaseUtils.databaseUtils.getDeployInfoOnOneNode(serviceName, serviceVersion, nodeId);
                int a = new Random().nextInt(deleteInstancesOnNode.size());
                connectToClient.deleteInstance(deleteInstancesOnNode.get(a).getPodId());
                MDatabaseUtils.databaseUtils.deleteDeployById(deleteInstancesOnNode.get(a).getPodId());
            }else{
                System.out.println("不能删除，最后一个支持依赖的实例");
            }
        }
    }

    /**
     *  choose node to delete instance
     * @param surpportMap
     * @param itSelfMap
     * @return delete instance Id
     */
    public static String deletOnWhicheNode(Map<String, Integer> surpportMap, Map<String, Integer> itSelfMap){
        String node = "";
        double max = 0;
        for(String string: surpportMap.keySet()){
            if(surpportMap.get(string)!=0  && itSelfMap.get(string)!=0){
                if(max <= (double)itSelfMap.get(string)/(double)surpportMap.get(string) ){
                    max = (double)itSelfMap.get(string)/(double)surpportMap.get(string);
                    node = string;
                }
            }else if(surpportMap.get(string)==0 && itSelfMap.get(string)!=0){
                return string;
            }else{
                continue;
            }
        }
        return node;
    }


    // 版本撤销
    public static void Revocationversion(String serviceName, String serviceVersion){
        serviceVersion = MSvcVersion.fromStr(serviceVersion).toString();
        List<MDependencyDao> list = MDatabaseUtils.databaseUtils.getSupportByServiceIdWithoutFun(serviceName, serviceVersion);
        List<MService> mServices = MDatabaseUtils.databaseUtils.getServiceByName(serviceName);
        MService mService = MDatabaseUtils.databaseUtils.getServiceById(serviceName+"_"+ serviceVersion);
        boolean ifcanbeChexiao = true;
        if(list.isEmpty() || list==null){
            MDatabaseUtils.databaseUtils.deleteService(mService);
            return;
        }else{
            for(MDependencyDao mDependencyDao:list){
                ifcanbeChexiao = false;
                if(mDependencyDao.getServiceDenpendencyVersion() != null){
                    String[] versios = mDependencyDao.getServiceDenpendencyVersion().split(",");
                    if(versios.length == 1){  //唯一依赖
                        String interfacePath = mDependencyDao.getServiceDependencyInterfaceName();
                        StringBuilder dependencyVersion = new StringBuilder();
                        for(MService mService1 : mServices){
                            if(mService1.getServiceInterfaceMap().keySet().contains(interfacePath) &&
                                    !mService1.getServiceVersion().toString().equals(versios[0])){
                                dependencyVersion.append(serviceVersion+",");
                            }
                        }
                        if(dependencyVersion.length() != 0){
                            ifcanbeChexiao = true;
                            dependencyVersion.deleteCharAt(dependencyVersion.lastIndexOf(","));
                            mDependencyDao.setServiceDenpendencyVersion(dependencyVersion.toString());
                            MDatabaseUtils.databaseUtils.deleteDependency(mDependencyDao);
                            MDatabaseUtils.databaseUtils.insertIntoDependency(mDependencyDao);
                        }else{
                            ifcanbeChexiao = false;
                            System.out.println(mDependencyDao + "  ：此版本依赖不能撤销：");
                            break;
                        }
                    }else{
                        ifcanbeChexiao = true;
                    }
                }else{ // 服务依赖
                    String interfacePath = mDependencyDao.getServiceDependencyInterfaceName();
                    if(mService.getServiceInterfaceMap().keySet().contains(interfacePath)){
                        int maxSla = 0;
                        for(MService mService1 : mServices){
                            if(!mService1.getServiceVersion().equals(serviceVersion)){
                                if(mService1.getServiceInterfaceMap().keySet().contains(interfacePath)){
                                    ifcanbeChexiao = true;
                                    if(mService1.getServiceInterfaceMap().get(interfacePath).getFuncDescription().getSla().getLevel() >= maxSla){
                                        maxSla = mService1.getServiceInterfaceMap().get(interfacePath).getFuncDescription().getSla().getLevel();
                                    }
                                    break;
                                }
                            }
                        }
                        if(maxSla <= mDependencyDao.getFunctionLevel()){
                            mDependencyDao.setFunctionLevel(maxSla);
                        }
                        MDatabaseUtils.databaseUtils.deleteDependency(mDependencyDao);
                        MDatabaseUtils.databaseUtils.insertIntoDependency(mDependencyDao);
                    }
                    if(!ifcanbeChexiao){
                        System.out.println("此版本微服务不能撤销");
                        break;
                    }
                }
            }
        }
        if(!ifcanbeChexiao){
            System.out.println("此版本微服务不能撤销");
        }
        MDatabaseUtils.databaseUtils.deleteDependencyByid(serviceName+"_"+serviceVersion);
        MDatabaseUtils.databaseUtils.deleteService(mService);
    }
}

