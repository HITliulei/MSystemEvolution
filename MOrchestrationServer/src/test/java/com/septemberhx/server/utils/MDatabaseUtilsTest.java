package com.septemberhx.server.utils;

import com.septemberhx.common.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2019/12/28
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MDatabaseUtilsTest {

    @Autowired
    private MDatabaseUtils databaseUtils;

    @Test
    public void test() {
        MParamer paramer = new MParamer();
        paramer.setMethod("method");
        paramer.setType("type");
        paramer.setDefaultObject("1");
        paramer.setRequestname("param");
        paramer.setName("param");
        List<MParamer> paramerList = new ArrayList<>();
        paramerList.add(paramer);

        MServiceInterface serviceInterface = new MServiceInterface();
        MFuncDescription funcDescription = new MFuncDescription();
        funcDescription.setSlaLevel(1);
        funcDescription.setFeatureName("test");
        serviceInterface.setFuncDescription(funcDescription);
        serviceInterface.setServiceId("test-service");
        serviceInterface.setReturnType("r");
        serviceInterface.setRequestMethod("POST");
        serviceInterface.setPatternUrl("/fa/s");
        serviceInterface.setId("test-service-interface-01");
        serviceInterface.setParams(paramerList);
        serviceInterface.setFunctionName("function");
        Map<String, MServiceInterface> interfaceMap = new HashMap<>();
        interfaceMap.put(serviceInterface.getId(), serviceInterface);

        MService service = new MService();
        service.setPort(8080);
        service.setImageUrl("dockerImage");
        service.setServiceVersion(MServiceVersion.fromStr("1.2.3"));
        service.setServiceName("test");
        service.setId("test-service");
        service.setGitUrl("git");
        service.setServiceInterfaceMap(interfaceMap);

        List<MService> services = MDatabaseUtils.databaseUtils.getAllServices();
        assertEquals(0, services.size());

        MDatabaseUtils.databaseUtils.insertService(service);
        List<MService> serviceList = MDatabaseUtils.databaseUtils.getAllServices();
        assertEquals(1, serviceList.size());
        assertEquals(service, serviceList.get(0));

        MService targetService = MDatabaseUtils.databaseUtils.getServiceById(service.getId());
        assertEquals(service, targetService);

        MDatabaseUtils.databaseUtils.deleteService(service);
        assertNull(MDatabaseUtils.databaseUtils.getServiceById(service.getId()));
    }

}
