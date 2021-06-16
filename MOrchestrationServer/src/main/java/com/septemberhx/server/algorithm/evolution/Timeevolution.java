package com.septemberhx.server.algorithm.evolution;

import com.septemberhx.common.bean.agent.MDeployPodRequest;
import com.septemberhx.common.bean.agent.MInstanceInfoBean;
import com.septemberhx.server.client.ConnectToClient;
import com.septemberhx.server.dao.MDependencyDao;
import com.septemberhx.server.dao.MDeployDao;
import com.septemberhx.server.utils.MDatabaseUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;

/**
 * @Author Lei
 * @Date 2020/3/17 21:53
 * @Version 1.0
 */

@Configuration
@EnableScheduling
public class Timeevolution {

    public static Map<MInstanceInfoBean, Integer> useRate = new HashMap<>();

    @Autowired
    ConnectToClient connectToClient;


    @Scheduled(cron = "0 0 12 * * ?")
    public void TimeToevolution(){
        // delete deploy but not run and update deploy database
        deleteButNotRun();
        // dependency
        evolution();
        // delete all useRate
        deleteAllUseRate();
    }

    public void deleteButNotRun(){
        List<MDeployDao> deployDaoList = MDatabaseUtils.databaseUtils.getAlldeployInfo();
        List<MInstanceInfoBean> instanceInfoBeans = connectToClient.getAllMintancesInfo();
        for(MDeployDao mDeployDao: deployDaoList){
            if(mDeployDao.getRegisterId() == null){
                connectToClient.deleteInstance(mDeployDao.getPodId());
                MDatabaseUtils.databaseUtils.deleteDeployById(mDeployDao.getPodId());
                continue;
            }
            boolean ifIn = false;
            for(MInstanceInfoBean mInstanceInfoBean: instanceInfoBeans){
                if(mDeployDao.getRegisterId().equalsIgnoreCase(mInstanceInfoBean.getRegistryId())){
                    ifIn = true;
                    break;
                }
            }
            if(!ifIn){
                MDatabaseUtils.databaseUtils.deleteDeployById(mDeployDao.getPodId());
            }
        }


    }
    public void evolution(){
        Map<String, Map<String, Integer>> serviceIdUseRate = getServiceIdUseRate();
        for(String serviceName:serviceIdUseRate.keySet()){
            Map<String, Integer> serviceVersionRate = serviceIdUseRate.get(serviceName);
            Integer sum = 0;
            for(String serviceVersion:serviceVersionRate.keySet()){
                sum = sum + serviceVersionRate.get(serviceVersion);
            }
            if(serviceVersionRate.size() != 1){
                Integer low = sum / (serviceVersionRate.size()*2);
                Integer hight = sum / (serviceVersionRate.size()/2);
                for(String serviceVersion:serviceVersionRate.keySet()){
                    if(serviceVersionRate.get(serviceVersion) < low){
                        Integer list = MDatabaseUtils.databaseUtils.getDeployByTrueserviceIdAndRun(serviceName+"_"+serviceVersion).size();
                        if(list >= 1){
                            MInstanceInfoBean mInstanceInfoBean = getLowestUseRate(serviceName, serviceVersion);
                            connectToClient.deleteInstance(mInstanceInfoBean.getDockerInfo().getInstanceId());
                        }
                    }else if(serviceVersionRate.get(serviceVersion) > hight){
                        MInstanceInfoBean mInstanceInfoBean = getHightestUseRate(serviceName, serviceVersion);
                        MDeployPodRequest mDeployPodRequest = new MDeployPodRequest();
                        mDeployPodRequest.setId(serviceName+"_"+serviceVersion);
                        mDeployPodRequest.setNodeId(mInstanceInfoBean.getDockerInfo().getNodeLabel());
                        mDeployPodRequest.setServiceName(serviceName);
                        mDeployPodRequest.setImageUrl(MDatabaseUtils.databaseUtils.getServiceById(serviceName+"_"+serviceVersion).getImageUrl());
                        mDeployPodRequest.setUniqueId(UUID.randomUUID().toString().substring(24));
                        connectToClient.deploy(mDeployPodRequest);
                    }else{
                        continue;
                    }
                }
            }
        }
    }

    public Map<String, Map<String, Integer>> getServiceIdUseRate(){
        Map<String, Map<String, Integer>> map = new HashMap<>();
        for(MInstanceInfoBean mInstanceInfoBean:useRate.keySet()){
            String serviceName = mInstanceInfoBean.getServiceName();
            String serviceVersion = mInstanceInfoBean.getServiceVersion();
            Map<String, Integer> s = map.get(serviceName);
            if(map.containsKey(serviceName)){
                if(s.keySet().contains(serviceVersion)){
                    s.put(serviceVersion,s.get(serviceVersion)+1);
                }else{
                    s.put(serviceVersion,1);
                }
            }else{
                if(s.keySet().contains(serviceVersion)){
                    s.put(serviceVersion,s.get(serviceVersion)+1);
                }else{
                    s.put(serviceVersion,1);
                }
            }
            map.put(serviceName, s);
        }
        return map;
    }


    public void deleteAllUseRate(){
        useRate.clear();
    }

    public MInstanceInfoBean getLowestUseRate(String serviceNmae, String serviceVersion){
        Map<MInstanceInfoBean, Integer> map = getByNameAndVersion(serviceNmae,serviceVersion);
        MInstanceInfoBean result = null;
        Integer a = 1000;
        for(MInstanceInfoBean mInstanceInfoBean:map.keySet()){
            if(a >= map.get(mInstanceInfoBean)){
                result = mInstanceInfoBean;
                a = map.get(mInstanceInfoBean);
            }
        }
        return result;
    }
    public MInstanceInfoBean getHightestUseRate(String serviceNmae, String serviceVersion){
        Map<MInstanceInfoBean, Integer> map = getByNameAndVersion(serviceNmae,serviceVersion);
        MInstanceInfoBean result = null;
        Integer a = 0;
        for(MInstanceInfoBean mInstanceInfoBean:map.keySet()){
            if(a <= map.get(mInstanceInfoBean)){
                result = mInstanceInfoBean;
                a = map.get(mInstanceInfoBean);
            }
        }
        return result;
    }

    public Map<MInstanceInfoBean, Integer> getByNameAndVersion(String serviceName, String serviceVersion){
        Map<MInstanceInfoBean, Integer> map = new HashMap<>();
        for(MInstanceInfoBean mInstanceInfoBean:useRate.keySet()){
            if(mInstanceInfoBean.getServiceName().equals(serviceName) && mInstanceInfoBean.getServiceVersion().equals(serviceVersion)){
                map.put(mInstanceInfoBean,useRate.get(mInstanceInfoBean));
            }
        }
        return map;
    }
}
