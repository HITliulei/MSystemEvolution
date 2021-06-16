package com.septemberhx.server.algorithm.deploy;

import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.MSvcInterface;
import com.septemberhx.common.service.MSvcVersion;
import com.septemberhx.server.dao.MDependencyDao;
import com.septemberhx.server.dao.MDeployDao;
import com.septemberhx.server.utils.MDatabaseUtils;

import java.util.*;

/**
 * @Author Lei
 * @Date 2020/3/21 11:40
 * @Version 1.0
 */
public class Core {

    /**
     * get dependency no deployed in cluster
     * @param mDependencyDaos Dependency relationship
     * @return No dependent version deployed to cluster
     */
    public static Map<String, Set<String>> noDeployedThedependenciesVerson(List<MDependencyDao> mDependencyDaos){
        Map<String, Set<String>> map = getDependencyVersionSet(mDependencyDaos);
        System.out.println("得到的依赖的微服务 + 版本为" + map);
        Map<String,Set<String>> result = new HashMap<>(map);
        for(String string: map.keySet()){
            Set<String> versions =map.get(string);
            for(String version:versions){
                List<MDeployDao> list = MDatabaseUtils.databaseUtils.getDeployByserviceId(string.split("_")[0].toLowerCase(), version);
                if(list.size() !=0 ){
                    result.remove(string);
                    break;
                }
            }
        }
        return result;
    }

    public static Map<String, Set<String>> DeployedThedependenciesVerson(List<MDependencyDao> mDependencyDaos){
        Map<String, Set<String>> map = getDependencyVersionSet(mDependencyDaos);
        Map<String,Set<String>> result = new HashMap<>(map);
        for(String string: map.keySet()){
            Set<String> versions =map.get(string);
            for(String version:versions){
                List<MDeployDao> list = MDatabaseUtils.databaseUtils.getDeployByserviceId(string.split("_")[0].toLowerCase(), version);
                // no depoy in cluster
                if(list.size() ==0 ){
                    result.remove(string);
                    break;
                }
            }
        }
        return result;
    }


    /**
     *  get dependency
     * @param mDependencyDaos Dependency relationship
     * @return Dependency relationship
     */
    public static Map<String, Set<String>> getDependencyVersionSet(List<MDependencyDao> mDependencyDaos){
        Map<String, Set<String>> map = new HashMap<>();
        for (MDependencyDao mDependencyDao: mDependencyDaos){
            if(mDependencyDao.getServiceDenpendencyName() != null){
                System.out.println("存在非功能性的依赖");
                map.put(mDependencyDao.getServiceDenpendencyName()+"_"+mDependencyDao.getServiceDependencyInterfaceName(), null);
            }else{ // 功能依赖
                System.out.println("存在功能性的依赖");
                List<MService> allService = MDatabaseUtils.databaseUtils.getAllServices();
                for(MService mService: allService){
                    Map<String, MSvcInterface> interfaceMap = mService.getServiceInterfaceMap();
                    for(String string: interfaceMap.keySet()){
                        if(mDependencyDao.getFunctionDescribe().equalsIgnoreCase(interfaceMap.get(string).getFuncDescription().getFunc().getFunctionName())
                                && interfaceMap.get(string).getFuncDescription().getSla().getLevel()>=mDependencyDao.getFunctionLevel()){
                            String id = mService.getServiceName()+"_"+ interfaceMap.get(string).getPatternUrl();
                            if(map.keySet().contains(id)){
                                Set<String> set = map.get(id);
                                set.add(mService.getServiceVersion().toString());
                                map.put(id,set);
                            }else{
                                Set<String> set = new HashSet<>();
                                set.add(mService.getServiceVersion().toString());
                                map.put(id,set);
                            }
                        }
                    }
                }
            }
        }
        for(String s: map.keySet()){
            if(map.get(s) != null){
                continue;
            }
            Set<String> set = new HashSet<>();
            for (MDependencyDao mDependencyDao: mDependencyDaos){
                if((mDependencyDao.getServiceDenpendencyName()+"_"+mDependencyDao.getServiceDependencyInterfaceName()).equals(s)){
                    String version =mDependencyDao.getServiceDenpendencyVersion();
                    // SvcVerDependency
                    if(version != null){
                        String[] versions = version.split(",");
                        for(String v: versions){
                            set.add(MSvcVersion.fromStr(v).toString());
                        }
                    }else{ //SvcSlaDependency
                        List<MService> allService = MDatabaseUtils.databaseUtils.getServiceByName(mDependencyDao.getServiceDenpendencyName());
                        for(MService mService: allService){
                            System.out.println("服务依赖版本集合为" +mService.getServiceInterfaceMap().keySet() );
                            if (mService.getServiceInterfaceMap().keySet().contains(mDependencyDao.getServiceDependencyInterfaceName())){
                                if(mService.getServiceInterfaceMap().get(mDependencyDao.getServiceDependencyInterfaceName()).getFuncDescription().getSla().getLevel() >= mDependencyDao.getFunctionLevel()){
                                    set.add(mService.getServiceVersion().toString());
                                }
                            }
                        }
                    }
                }
            }
            map.put(s,set);
        }
        return map;
    }
}
