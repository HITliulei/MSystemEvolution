package com.septemberhx.server.utils;

import com.septemberhx.common.service.MParamer;
import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.MServiceInterface;
import com.septemberhx.server.dao.MInterfaceDao;
import com.septemberhx.server.dao.MParamDao;
import com.septemberhx.server.dao.MServiceDao;
import com.septemberhx.server.mapper.InterfacesMapper;
import com.septemberhx.server.mapper.ParamsMapper;
import com.septemberhx.server.mapper.ServicesMapper;
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

            for (MServiceInterface serviceInterface : service.getServiceInterfaceMap().values()) {
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
            for (MServiceInterface serviceInterface : service.getServiceInterfaceMap().values()) {
                databaseUtils.paramsMapper.deleteByInterfaceId(serviceInterface.getId());
            }
            databaseUtils.interfacesMapper.deleteByServiceId(service.getId());
            databaseUtils.servicesMapper.deleteById(service.getId());
            return null;
        });
    }

    public void updateServiceImageUrl(String serviceId, String imageUrl) {
        databaseUtils.servicesMapper.updateImageUrl(serviceId, imageUrl);
    }

    public MService getServiceById(String serviceId) {
        MServiceDao serviceDao = databaseUtils.servicesMapper.getById(serviceId);
        if (serviceDao == null) return null;

        return this.getServiceByDao(serviceDao);
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
        Map<String, MServiceInterface> interfaceMap = new HashMap<>();
        for (MInterfaceDao interfaceDao : interfaceDaoList) {
            MServiceInterface serviceInterface = interfaceDao.toDto();
            List<MParamDao> paramDaoList = databaseUtils.paramsMapper.getByInterfaceId(serviceInterface.getId());
            paramDaoList.sort(Comparator.comparingInt(MParamDao::getOrder));
            List<MParamer> paramerList = new ArrayList<>();
            for (MParamDao paramDao : paramDaoList) {
                paramerList.add(paramDao.toDto());
            }
            serviceInterface.setParams(paramerList);
            interfaceMap.put(serviceInterface.getId(), serviceInterface);
        }
        service.setServiceInterfaceMap(interfaceMap);
        return service;
    }
}
