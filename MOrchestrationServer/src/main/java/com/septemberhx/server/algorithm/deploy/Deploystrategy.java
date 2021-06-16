package com.septemberhx.server.algorithm.deploy;

import com.septemberhx.common.bean.agent.MDeployPodRequest;
import com.septemberhx.common.bean.instance.MDeployVerionWithoutNode;
import com.septemberhx.common.bean.instance.MDeployVersion;
import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.MSvcVersion;
import com.septemberhx.server.client.ConnectToClient;
import com.septemberhx.server.dao.MDependencyDao;
import com.septemberhx.server.dao.MDeployDao;
import com.septemberhx.server.utils.MDatabaseUtils;

import java.util.*;

/**
 * @Author Lei
 * @Date 2020/3/16 14:50
 * @Version 1.0
 */

public class Deploystrategy {
    /**
     *  deploy one instance on one node
     * @param mDeployVersion
     * @param connectToClient
     */
    public static void deployOneInstanceOnNode(MDeployVersion mDeployVersion, ConnectToClient connectToClient){
        String serviceName = mDeployVersion.getServiceName();
        String version = MSvcVersion.fromStr(mDeployVersion.getServiceVersion()).toString();
        String serviceId = serviceName+"_"+version;
        List<MDependencyDao> list = MDatabaseUtils.databaseUtils.getDepnecy(serviceName+"_"+version);
        if(list != null && !list.isEmpty()){
            Map<String, Set<String>> map = Core.noDeployedThedependenciesVerson(list);
            System.out.println("需要部署的依赖微服务为：" + map);
            for(String string: map.keySet()){
                String deployVersion = getVersion(map.get(string));
                MDeployVersion mDeployVersion1 = new MDeployVersion();
                mDeployVersion1.setNodeid(mDeployVersion.getNodeid());
                mDeployVersion1.setServiceName(string.split("_")[0]);
                mDeployVersion1.setServiceVersion(deployVersion);
                deployOneInstanceOnNode(mDeployVersion1, connectToClient);
            }
        }
        deploy(mDeployVersion, connectToClient);
    }


    public static void deployServiceVersion(MDeployVerionWithoutNode mDeployVerionWithoutNode, ConnectToClient connectToClient){
        String serviceName = mDeployVerionWithoutNode.getServiceName();
        String serviceVersion = MSvcVersion.fromStr(mDeployVerionWithoutNode.getServiceVersion()).toString();
        List<MDeployDao> mDeployDaos = MDatabaseUtils.databaseUtils.getDeployByserviceIdAndRun(mDeployVerionWithoutNode.getServiceName(),
                MSvcVersion.fromStr(mDeployVerionWithoutNode.getServiceVersion()).toString());
        List<MDependencyDao> mDependencyDaos = MDatabaseUtils.databaseUtils.getDepnecy(serviceName+"_"+serviceVersion);
        List<MDependencyDao> mSupportDaos = MDatabaseUtils.databaseUtils.getSupportByServiceId(serviceName,serviceVersion);
        Map<String, String> node = connectToClient.getAllNodeLabel();
        if(mDeployDaos.isEmpty()){
            // random choose one node to deploy one id
            String randomNode = node.get(node.keySet().toArray()[0].toString());
            deployOneInstanceOnNode(new MDeployVersion(serviceName, serviceVersion, randomNode), connectToClient);
            Map<String,Integer> suport = getSupportByNode(mSupportDaos, node);
            System.out.println("各个节点支持的个数" + suport);

            Map<String, Integer> dependency = getDependencyByNode(mDependencyDaos, node);
            System.out.println("各个节点依赖的个数"+ dependency);
            Map<String, Integer> toBeDeployed = toBeDeployed(dependency, suport);
            for(String string: toBeDeployed.keySet()){
                for(int i =0;i<toBeDeployed.get(string);i++){
                    deploy(new MDeployVersion(serviceName, serviceVersion, string), connectToClient);
                }
            }
        }else{
            for(String s:node.keySet()){
                deploy(new MDeployVersion(serviceName, serviceVersion, s), connectToClient);
            }
        }
    }



    public static Map<String, Integer> getSupportByNode(List<MDependencyDao> mSupportDaos, Map<String, String> node){
        Map<String, Integer> map = new HashMap<>();
        for(String string:node.keySet()){
            map.put(node.get(string),0);
        }
        for(MDependencyDao mSupportDao:mSupportDaos){
            List<MDeployDao> list = MDatabaseUtils.databaseUtils.getDeployByTrueserviceIdAndRun(mSupportDao.getServiceId());
            for(MDeployDao mDeployDao:list){
                int a = map.get(mDeployDao.getNodeId());
                map.put(mDeployDao.getNodeId(), a+1);
            }
        }
        return map;
    }

    public static Map<String, Integer> getDependencyByNode(List<MDependencyDao> mDependencyDaos, Map<String, String> node){
        Map<String, Integer> map = new HashMap<>();
        for(String string:node.keySet()){
            map.put(node.get(string),0);
        }
        for(MDependencyDao mDependencyDao:mDependencyDaos){
            if(mDependencyDao.getServiceDenpendencyName() == null){
                continue;
            }
            if(mDependencyDao.getServiceDenpendencyVersion() == null){
                List<MService> list = MDatabaseUtils.databaseUtils.getServiceByName(mDependencyDao.getServiceDenpendencyName());
                for(MService mService: list){
                    if(mService.getServiceInterfaceMap().keySet().contains(mDependencyDao.getServiceDependencyInterfaceName())){
                        List<MDeployDao> mDeployDaos = MDatabaseUtils.databaseUtils.getDeployByserviceIdAndRun(mDependencyDao.getServiceDenpendencyName(), mService.getServiceVersion().toString());
                        for(MDeployDao mDeployDao:mDeployDaos){
                            int a = map.get(mDeployDao.getNodeId());
                            map.put(mDeployDao.getNodeId(), a+1);
                        }
                    }
                }
            }else{
                Set<String> versions = MDependencyDao.getVersions(mDependencyDao.getServiceDenpendencyVersion());
                for(String version: versions){
                    List<MDeployDao> list = MDatabaseUtils.databaseUtils.getDeployByserviceIdAndRun(mDependencyDao.getServiceDenpendencyName(), version);
                    for(MDeployDao mDeployDao:list){
                        int a = map.get(mDeployDao.getNodeId());
                        map.put(mDeployDao.getNodeId(), a+1);
                    }
                }
            }
        }
        return map;
    }

    public static Map<String, Integer> toBeDeployed(Map<String, Integer> dependency, Map<String, Integer> surport){
        return new GenerationAlgorithm(dependency,surport).evolution();
    }


    /**
     * get the highest version
     * @param set version set
     * @return the highest version
     */
    public static String getVersion(Set<String> set){
        String version ="";
        int v = 0;
        for(String string: set){
            Integer v1 = Integer.parseInt(string.replaceAll("\\.",""));
            if(v1 >= v){
                v = v1;
                version = string;
            }
        }
        return version;
    }


    public static void deploy(MDeployVersion mDeployVersion, ConnectToClient connectToClient){
        String serviceId = mDeployVersion.getServiceName() + "_"+ MSvcVersion.fromStr(mDeployVersion.getServiceVersion()).toString();
        System.out.println("发送的部署信息为 ： " + mDeployVersion);
        String uniteid = UUID.randomUUID().toString().substring(24);
        String imageurl = MDatabaseUtils.databaseUtils.getServiceDao(serviceId).getServiceImage();
        MDeployPodRequest mDeployPodRequest = new MDeployPodRequest(serviceId, mDeployVersion.getNodeid(), uniteid, mDeployVersion.getServiceName().toLowerCase(), imageurl);
        connectToClient.deploy(mDeployPodRequest);
        MDatabaseUtils.databaseUtils.deploy(new MDeployDao(uniteid, mDeployVersion.getNodeid(), mDeployVersion.getServiceName().toLowerCase(), MSvcVersion.fromStr(mDeployVersion.getServiceVersion()).toString()));
        connectToClient.register(mDeployPodRequest);
    }
}
