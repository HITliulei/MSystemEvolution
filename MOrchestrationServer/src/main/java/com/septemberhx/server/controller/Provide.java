package com.septemberhx.server.controller;

import com.septemberhx.common.bean.agent.MInstanceInfoBean;
import com.septemberhx.common.config.MConfig;
import com.septemberhx.common.service.MFunc;
import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.MSvcInterface;
import com.septemberhx.common.service.MSvcVersion;
import com.septemberhx.server.algorithm.evolution.Timeevolution;
import com.septemberhx.server.client.ConnectToClient;
import com.septemberhx.server.dao.MDependencyDao;
import com.septemberhx.server.dao.MDeployDao;
import com.septemberhx.server.dao.MServiceDao;
import com.septemberhx.server.utils.MDatabaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @Author Lei
 * @Date 2020/3/23 21:53
 * @Version 1.0
 */

@RestController
@RequestMapping(MConfig.MCENTER_PROVIDE)
public class Provide {

    @Autowired
    private ConnectToClient connectToClient;

    @GetMapping(MConfig.MCENTER_PROVIDE_GETALLSERVICENAME)
    public List<String> getAllServiceName(){
        System.out.println("接口访问");
        Set<String> result = new HashSet<>();
        List<MService> list = MDatabaseUtils.databaseUtils.getAllServices();
        for(MService mService:list){
            result.add(mService.getServiceName());
        }
        return new ArrayList<>(result);
    }

    @GetMapping("/getServiceByNameAndVersion")
    public MService getServiceByNameAndVersion(@RequestParam("serviceName")String serviceName,@RequestParam("serviceVersion")String version){
        System.out.println("接口访问");
        MService mService = MDatabaseUtils.databaseUtils.getServiceById(serviceName+"_"+ MSvcVersion.fromStr(version).toString());
        return mService;
    }

    @GetMapping("/getServicseByname")
    public List<MService> getServiceByName(@RequestParam("serviceName")String serviceName){
        System.out.println("接口访问");
        return MDatabaseUtils.databaseUtils.getServiceByName(serviceName);
    }

    @PostMapping("/updateUseUseinfo")
    public void updateUseUseinfo(@RequestBody MInstanceInfoBean mInstanceInfoBean){
        System.out.println("接口访问");
        boolean ifIn = false;
        for(MInstanceInfoBean mInstanceInfoBean1: Timeevolution.useRate.keySet()){
            if(mInstanceInfoBean1.equals(mInstanceInfoBean)){
                Timeevolution.useRate.put(mInstanceInfoBean1,Timeevolution.useRate.get(mInstanceInfoBean1) +1);
                ifIn = true;
            }
        }
        if(!ifIn){
            Timeevolution.useRate.put(mInstanceInfoBean,1);
        }
    }

    @GetMapping("/getNode")
    public String getNode(@RequestParam("serviceId")String serviceId, @RequestParam("ip")String ip){
        String servieName = serviceId.split("_")[0];
        String serviceVersion = serviceId.split("_")[1];
        List<MDeployDao> list = MDatabaseUtils.databaseUtils.getDeployByTrueserviceIdAndRun(serviceId);
        for(MDeployDao mDeployDao:list){
            if(mDeployDao.getIpAddress().equals(ip) && mDeployDao.getServiceName().equalsIgnoreCase(servieName) && mDeployDao.getServiceVersion().equals(serviceVersion)){
                return mDeployDao.getNodeId();
            }
        }
        return "";
    }


    @GetMapping("/getMServiceByDependency")
    public Map<String, List<MService>> getMServiceByDependenvy(@RequestParam("name")String name, @RequestParam("id")String id){
        Map<String, List<MService>> map = new HashMap<>();
        MDependencyDao mDependencyDao = MDatabaseUtils.databaseUtils.getDependencyByNameAndId(name, id);
        if(mDependencyDao.getServiceDenpendencyName() != null){
            List<MService> list = new ArrayList<>();
            Integer sla = mDependencyDao.getFunctionLevel();
            if(sla == null){
                sla = 0;
            }
            if(mDependencyDao.getServiceDenpendencyVersion() != null){
                Set<String> versions = MDependencyDao.getVersions(mDependencyDao.getServiceDenpendencyVersion());
                for(String version: versions){
                    list.add(MDatabaseUtils.databaseUtils.getServiceById(mDependencyDao.getServiceDenpendencyName()+"_"+MSvcVersion.fromStr(version).toString()));
                }
            }else{
                List<MService> mServices = MDatabaseUtils.databaseUtils.getServiceByName(mDependencyDao.getServiceDenpendencyName());
                for(MService mService:mServices){
                    for(String interfaceName: mService.getServiceInterfaceMap().keySet()){
                        if(interfaceName.equals(mDependencyDao.getServiceDependencyInterfaceName()) && mService.getServiceInterfaceMap().get(interfaceName).getFuncDescription().getSla().getLevel()>=sla){
                            list.add(mService);
                        }
                    }
                }
            }
            map.put(mDependencyDao.getServiceDependencyInterfaceName(), list);
        }else{
            if(mDependencyDao.getFunctionDescribe() == null){
                return null;
            }
            List<MService> mServices = MDatabaseUtils.databaseUtils.getAllServices();
            for(MService mService:mServices){
                Map<String, MSvcInterface> maps = mService.getServiceInterfaceMap();
                for(String insterfaceName: maps.keySet()){
                    if(maps.get(insterfaceName).getFuncDescription().getFunc().equals(new MFunc(mDependencyDao.getFunctionDescribe()))){
                        if(map.containsKey(insterfaceName)){
                            map.get(insterfaceName).add(mService);
                        }else{
                            List<MService> list = new ArrayList<>();
                            list.add(mService);
                            map.put(insterfaceName,mServices);
                        }
                        break;
                    }
                }
            }
        }
        return map;
    }
    @GetMapping("/getAllDeployInfo1")
    public List<MDeployDao> getalldeploy(){
        return MDatabaseUtils.databaseUtils.getAlldeployInfo();
    }




    @GetMapping("/getAllDeployInfo")
    public Map<String, Map<String, Integer>> getAllDeployInfo(){
        Map<String, Map<String, Integer>> map = new HashMap<>();
        Map<String, String> nodelabl = connectToClient.getAllNodeLabel();
        for (String string : nodelabl.keySet()){
            Map<String, Integer> insert = new HashMap<>();
            map.put(nodelabl.get(string),insert);
        }
        List<MDeployDao> list = MDatabaseUtils.databaseUtils.getAlldeployInfo();
        for(MDeployDao mDeployDao : list){
            Map<String, Integer> insert = map.get(mDeployDao.getNodeId());
            if(insert.keySet().contains(mDeployDao.getServiceName().toLowerCase()+"_"+mDeployDao.getServiceVersion())){
                Integer a = insert.get(mDeployDao.getServiceName().toLowerCase()+"_"+mDeployDao.getServiceVersion());
                insert.put(mDeployDao.getServiceName().toLowerCase()+"_"+mDeployDao.getServiceVersion(), a+1);
            }else{
                insert.put(mDeployDao.getServiceName().toLowerCase()+"_"+mDeployDao.getServiceVersion(),1);
            }
            map.put(mDeployDao.getNodeId(), insert);
        }
        return map;
    }


    @GetMapping("/getUsesituation")
    public Map<MInstanceInfoBean, Integer> getUse(){
        return Timeevolution.useRate;
    }




}
