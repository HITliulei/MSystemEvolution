package com.septemberhx.server.utils;

import com.netflix.discovery.converters.Auto;
import com.septemberhx.common.factory.MBaseSvcDependencyFactory;
import com.septemberhx.common.service.*;
import com.septemberhx.common.service.dependency.*;
import com.septemberhx.server.dao.*;
import com.septemberhx.server.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.*;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2019/12/28
 *
 * Transaction here
 */
@Component
public class MDatabaseUtils {

    @Autowired
    private InterfacesMapper interfacesMapper;

    @Autowired
    private ParamsMapper paramsMapper;

    @Autowired
    private ServicesMapper servicesMapper;

    @Autowired
    private DependencyMapper dependencyMapper;

    @Autowired
    private MDeployMapper mDeployMapper;

    @Autowired
    DataSource dataSource;

    public static MDatabaseUtils databaseUtils;

    private DataSourceTransactionManager transactionManager;

    @PostConstruct
    public void init() {
        MDatabaseUtils.databaseUtils = this;
        this.transactionManager = new DataSourceTransactionManager(this.dataSource);
    }

    public void insertService(MService service) {
        MServiceDao serviceDao = MServiceDao.fromDto(service);
        TransactionTemplate transactionTemplate = new TransactionTemplate(databaseUtils.transactionManager);
        transactionTemplate.execute(txStatus -> {
            databaseUtils.servicesMapper.insert(serviceDao);

            for (MSvcInterface serviceInterface : service.getServiceInterfaceMap().values()) {
                MInterfaceDao interfaceDao = MInterfaceDao.fromDto(serviceInterface);
                databaseUtils.interfacesMapper.insert(interfaceDao);

                for (int i = 0; i < serviceInterface.getParams().size(); ++i) {
                    MParamDao paramDao = MParamDao.fromDto(
                            serviceInterface.getId(),
                            serviceInterface.getParams().get(i),
                            i
                    );
                    databaseUtils.paramsMapper.insert(paramDao);
                }
            }
            return null;
        });
    }

    public void deleteService(MService service) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(databaseUtils.transactionManager);
        transactionTemplate.execute(txStatus -> {
            for (MSvcInterface serviceInterface : service.getServiceInterfaceMap().values()) {
                databaseUtils.paramsMapper.deleteByInterfaceId(serviceInterface.getId());
            }
            databaseUtils.interfacesMapper.deleteByServiceId(service.getId());

            databaseUtils.servicesMapper.deleteById(service.getId());
            databaseUtils.deleteDependencyByid(service.getId());
            return null;
        });
    }

    public void updateServiceImageUrl(String serviceId, String imageUrl) {
        databaseUtils.servicesMapper.updateImageUrl(serviceId, imageUrl);
    }

    public MService getServiceById(String serviceId) {
        MServiceDao serviceDao = databaseUtils.servicesMapper.getById(serviceId);
        System.out.println();
        if (serviceDao == null) {return null;}

        return this.getServiceByDao(serviceDao);
    }

    public MServiceDao getServiceDao(String serviceId){
        return databaseUtils.servicesMapper.getById(serviceId);
    }

    public List<MService> getServiceByName(String serviceName){
        List<MServiceDao> list = databaseUtils.servicesMapper.getByName(serviceName);
        List<MService> result = new ArrayList<>();
        for(MServiceDao mServiceDao : list){
            result.add(this.getServiceByDao(mServiceDao));
        }
        return result;
    }

    public List<MService> getAllServices() {
        List<MService> resultList = new ArrayList<>();
        for (MServiceDao serviceDao : databaseUtils.servicesMapper.getAll()) {
            resultList.add(this.getServiceByDao(serviceDao));
        }
        return resultList;
    }

    private MService getServiceByDao(MServiceDao serviceDao) {
        MService service = serviceDao.toDto();
        List<MInterfaceDao> interfaceDaoList = databaseUtils.interfacesMapper.getByServiceId(service.getId());
        Map<String, MSvcInterface> interfaceMap = new HashMap<>();
        for (MInterfaceDao interfaceDao : interfaceDaoList) {
            MSvcInterface serviceInterface = interfaceDao.toDto();
            List<MParamDao> paramDaoList = databaseUtils.paramsMapper.getByInterfaceId(serviceInterface.getId());
            paramDaoList.sort(Comparator.comparingInt(MParamDao::getOrder));
            List<MParamer> paramerList = new ArrayList<>();
            for (MParamDao paramDao : paramDaoList) {
                paramerList.add(paramDao.toDto());
            }
            serviceInterface.setParams(paramerList);
//            interfaceMap.put(serviceInterface.getId(), serviceInterface);
            interfaceMap.put(serviceInterface.getPatternUrl(),serviceInterface);
        }
        // get dependency

        List<MDependencyDao> mDependencyDaos = databaseUtils.dependencyMapper.getServiceDenpendency(service.getId());
        Map<String, Map<String, BaseSvcDependency>> dependencyMaps = new HashMap<>();
        for(MDependencyDao mDependencyDao:mDependencyDaos){
            BaseSvcDependency baseSvcDependency = MBaseSvcDependencyFactory.createBaseSvcDependency(
                    mDependencyDao.getDependencyId(),
                    mDependencyDao.getFunctionDescribe(),
                    mDependencyDao.getFunctionLevel(),
                    mDependencyDao.getServiceDenpendencyName(),
                    mDependencyDao.getServiceDependencyInterfaceName(),
                    mDependencyDao.getServiceDenpendencyVersion()==null?null:new ArrayList(MDependencyDao.getVersions(mDependencyDao.getServiceDenpendencyVersion())));
            if(dependencyMaps.keySet().contains(mDependencyDao.getDependencyName())){
                Map<String, BaseSvcDependency> dependencyMap = dependencyMaps.get(mDependencyDao.getDependencyName());
                dependencyMap.put(mDependencyDao.getDependencyId(), baseSvcDependency);
                dependencyMaps.put(mDependencyDao.getDependencyName(),dependencyMap);
            }else{
                Map<String, BaseSvcDependency> dependencyMap = new HashMap<>();
                dependencyMap.put(mDependencyDao.getDependencyId(), baseSvcDependency);
                dependencyMaps.put(mDependencyDao.getDependencyName(),dependencyMap);
            }
        }
        service.setMSvcDepDesc(new MSvcDepDesc(service.getId(), service.getServiceName(), dependencyMaps));
        service.setServiceInterfaceMap(interfaceMap);
        return service;
    }


    //dependency
    public void insertDependency(MSvcDepDesc mSvcDepDesc){
        for(String dependencyName : mSvcDepDesc.getDependencyMaps().keySet()){
            Map<String, BaseSvcDependency> map = mSvcDepDesc.getDependencyMaps().get(dependencyName);
            for(String dependencyId:map.keySet()){
                MDependencyDao mDependencyDao = new MDependencyDao();
                mDependencyDao.setDependencyName(dependencyName);
                mDependencyDao.setDependencyId(dependencyId);
                mDependencyDao.setServiceId(mSvcDepDesc.getServiceId());
                // 依赖信息
                BaseSvcDependency baseSvcDependency = map.get(dependencyId);
                if(baseSvcDependency.getVersionSet()!=null){
                    Set<MSvcVersion> versionSet = baseSvcDependency.getVersionSet();
                    StringBuilder version = new StringBuilder();
                    for(MSvcVersion mSvcVersion : versionSet){
                        version.append(mSvcVersion.toString());
                        version.append(",");
                    }
                    version.deleteCharAt(version.lastIndexOf(","));
                    mDependencyDao.setServiceDenpendencyName(baseSvcDependency.getServiceName());
                    mDependencyDao.setServiceDependencyInterfaceName(baseSvcDependency.getPatternUrl());
                    mDependencyDao.setServiceDenpendencyVersion(version.toString());
                }else if(baseSvcDependency.getServiceName()!=null){
                    mDependencyDao.setServiceDenpendencyName(baseSvcDependency.getServiceName());
                    mDependencyDao.setServiceDependencyInterfaceName(baseSvcDependency.getPatternUrl());
                    mDependencyDao.setFunctionLevel(baseSvcDependency.getSla().getLevel());
                }else{
                    mDependencyDao.setFunctionDescribe(baseSvcDependency.getFunc().getFunctionName());
                    mDependencyDao.setFunctionLevel(baseSvcDependency.getSla().getLevel());
                }
                databaseUtils.dependencyMapper.insert(mDependencyDao);
            }
        }
    }

    public void deleteDependencyByid(String serviceId){
        databaseUtils.dependencyMapper.deleteServiceDenpendency(serviceId);
    }

    public List<MDependencyDao> getDepnecy(String serviceId){
        return databaseUtils.dependencyMapper.getServiceDenpendency(serviceId);
    }

    public List<MDependencyDao> getAllDepnecy(){
        return databaseUtils.dependencyMapper.selectAlldependency();
    }
    public MDependencyDao getDependencyByNameAndId(String name, String id){
        return databaseUtils.dependencyMapper.getServiceDependencyDaoByNameAndId(name, id);
    }

    public List<MDependencyDao> getSupportByServiceId(String serviceName, String serviveVersion){
        MService mService = databaseUtils.getServiceById(serviceName+"_"+serviveVersion);
        Map<String, MSvcInterface> map = mService.getServiceInterfaceMap();
        List<MDependencyDao> result = new ArrayList<>();
        List<MDependencyDao> list =  databaseUtils.dependencyMapper.selectAlldependency();
        for(MDependencyDao mDependencyDao: list){
            System.out.println(mDependencyDao);
            if(mDependencyDao.getServiceDenpendencyVersion() != null){
                if(mDependencyDao.getServiceDenpendencyName() != null && mDependencyDao.getServiceDenpendencyName().equalsIgnoreCase(serviceName)){
                    if(mDependencyDao.getServiceDenpendencyVersion().contains(serviveVersion)){
                        System.out.println("版本依赖： "+ mDependencyDao.getServiceId());
                        result.add(mDependencyDao);
                    }

                }
            }else if(mDependencyDao.getServiceDenpendencyVersion() == null && mDependencyDao.getServiceDenpendencyName()!=null){
                if( mDependencyDao.getServiceDenpendencyName().equalsIgnoreCase(serviceName)){
                    System.out.println("服务依赖： "+ mDependencyDao.getServiceId());
                    if(map.keySet().contains(mDependencyDao.getServiceDependencyInterfaceName())){
                        if(map.get(mDependencyDao.getServiceDependencyInterfaceName()).getFuncDescription().getSla().getLevel()>=mDependencyDao.getFunctionLevel()){
                            result.add(mDependencyDao);
                        }
                    }
                }
            }else{
                for(String string:map.keySet()){
                    MFuncDescription mFuncDescription = map.get(string).getFuncDescription();
                    if(mFuncDescription.getFunc().getFunctionName().equalsIgnoreCase(mDependencyDao.getFunctionDescribe()) && mFuncDescription.getSla().getLevel()>=mDependencyDao.getFunctionLevel()){
                        System.out.println("功能依赖： "+ mDependencyDao.getServiceId());
                        result.add(mDependencyDao);
                    }
                }
            }
        }
        return result;
    }


    public List<MDependencyDao> getSupportByServiceIdWithoutFun(String serviceName, String serviveVersion){
        MService mService = databaseUtils.getServiceById(serviceName+"_"+serviveVersion);
        Map<String, MSvcInterface> map = mService.getServiceInterfaceMap();
        List<MDependencyDao> result = new ArrayList<>();
        List<MDependencyDao> list =  databaseUtils.dependencyMapper.selectAlldependency();
        for(MDependencyDao mDependencyDao: list){
            System.out.println(mDependencyDao);
            if(mDependencyDao.getServiceDenpendencyVersion() != null){
                if(mDependencyDao.getServiceDenpendencyName() != null && mDependencyDao.getServiceDenpendencyName().equalsIgnoreCase(serviceName)){
                    if(mDependencyDao.getServiceDenpendencyVersion().contains(serviveVersion)){
                        result.add(mDependencyDao);
                    }
                }
            }else if(mDependencyDao.getServiceDenpendencyVersion() == null && mDependencyDao.getServiceDenpendencyName()!=null){
                if( mDependencyDao.getServiceDenpendencyName().equalsIgnoreCase(serviceName)){
                    if(map.keySet().contains(mDependencyDao.getServiceDependencyInterfaceName())){
                        if(map.get(mDependencyDao.getServiceDependencyInterfaceName()).getFuncDescription().getSla().getLevel()>=mDependencyDao.getFunctionLevel()){
                            result.add(mDependencyDao);
                        }
                    }
                }
            }
        }
        return result;
    }

    public void updateDependency(MDependencyDao mDependencyDao){
        databaseUtils.dependencyMapper.updateDependency(mDependencyDao);
    }

    public void insertIntoDependency(MDependencyDao mDependencyDao){
        databaseUtils.dependencyMapper.insert(mDependencyDao);
    }

    public void deleteDependency(MDependencyDao mDependencyDao){
        databaseUtils.dependencyMapper.deleteByNameAndId(mDependencyDao.getDependencyName(), mDependencyDao.getDependencyId());
    }




    // deploy and delete
    public List<MDeployDao> getAlldeployInfo(){
        return mDeployMapper.getAlldeployInfo();
    }

    public MDeployDao getdeployInfo(String podId){
        return mDeployMapper.getDeployInfo(podId);
    }


    public List<MDeployDao> getDeployByserviceName(String serviceName){
        return mDeployMapper.getDeployByserviceName(serviceName.toLowerCase());
    }
    public List<MDeployDao> getDeployByserviceId(String serviceName, String serviceVersion){
        return mDeployMapper.getDeployByserviceId(serviceName.toLowerCase(), serviceVersion);
    }
    public List<MDeployDao> getDeployByTrueserviceId(String serviceId){
        String serviceName = serviceId.split("_")[0].toLowerCase();
        String serviceVersion = serviceId.split("_")[1];
        return mDeployMapper.getDeployByserviceId(serviceName, serviceVersion);
    }
    public List<MDeployDao> getDeployInfoOnOneNode(String serviceName, String serviceVersion, String nodeId){
        List<MDeployDao> list = new ArrayList<>();
        List<MDeployDao> mDeployDaos = getDeployByserviceId(serviceName.toLowerCase(), serviceVersion);
        for(MDeployDao mDeployDao: mDeployDaos){
            if(mDeployDao.getNodeId().equals(nodeId)){
                list.add(mDeployDao);
            }
        }
        return list;
    }

    public List<MDeployDao> getDeployByserviceIdAndRun(String serviceName, String serviceVersion){
        List<MDeployDao> list =  mDeployMapper.getDeployByserviceId(serviceName.toLowerCase(), serviceVersion);
        List<MDeployDao> result = new ArrayList<>();
        for(MDeployDao mDeployDao:list){
            if(mDeployDao.getRegisterId() != null){
                result.add(mDeployDao);
            }
        }
        return result;
    }
    public List<MDeployDao> getDeployByTrueserviceIdAndRun(String serviceId){
        String serviceName = serviceId.split("_")[0];
        String serviceVersion = serviceId.split("_")[1];
        List<MDeployDao> list =  mDeployMapper.getDeployByserviceId(serviceName.toLowerCase(), serviceVersion);
        List<MDeployDao> result = new ArrayList<>();
        for(MDeployDao mDeployDao:list){
            if(mDeployDao.getRegisterId() != null){
                result.add(mDeployDao);
            }
        }
        return result;
    }
    public List<MDeployDao> getDeployInfoOnOneNodeAndRun(String serviceName, String serviceVersion, String nodeId){
        List<MDeployDao> list = new ArrayList<>();
        List<MDeployDao> mDeployDaos = getDeployByserviceId(serviceName.toLowerCase(), serviceVersion);
        for(MDeployDao mDeployDao: mDeployDaos){
            if(mDeployDao.getNodeId().equals(nodeId) && mDeployDao.getRegisterId()!=null){
                list.add(mDeployDao);
            }
        }
        return list;
    }
    public void updateRegister(MDeployDao mDeployDao){
        System.out.println("update deploy : " + mDeployDao);
        mDeployMapper.updateRegister(mDeployDao);
    }
    public void deploy(MDeployDao mDeployDao){
        mDeployMapper.insertDeploy(mDeployDao);
    }
    public void deleteByname(String serviceName){
        mDeployMapper.deleteByName(serviceName);
    }
    public void deleteDeployById(String id){
        mDeployMapper.deleteById(id);
    }
    public void deleteInstanceByServiceId(String name, String v){
        mDeployMapper.delteByServiceId(name, v);
    }



}
