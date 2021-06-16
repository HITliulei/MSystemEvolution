package com.septemberhx.server.controller.ClusterController;

import com.septemberhx.common.service.MSvcVersion;
import com.septemberhx.server.algorithm.deploy.Deletestrategy;
import com.septemberhx.server.algorithm.deploy.Deploystrategy;
import com.septemberhx.server.client.ConnectToClient;
import com.septemberhx.server.dao.MDeployDao;
import com.septemberhx.server.utils.MDatabaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author Lei
 * @Date 2020/3/16 14:16
 * @Version 1.0
 */
@RestController
@RequestMapping("/delete")
public class Deplete {

    @Autowired
    private ConnectToClient connectToClient;

    @PostMapping("/byNameAndVersion")
    public void deleteOneInstanceByNameAdVersion(@RequestParam("serviceName")String name, @RequestParam("serviceVersion")String version){
        version = MSvcVersion.fromStr(version).toString();
        Deletestrategy.deleteOneinstance(name,version,connectToClient);
    }

    /**
     * undo A version of one microservice
     * @param serviceId the id of the specific version of microservice
     */
    @RequestMapping("/undoAversion/{Id}")
    public void deleteallInstance(@PathVariable("Id") String serviceId){
        String serviceName = serviceId.split("_")[0];
        String serviceVersion = MSvcVersion.fromStr(serviceId.split("_")[1]).toString();
        List<MDeployDao> list = MDatabaseUtils.databaseUtils.getDeployByserviceId(serviceName, serviceVersion);
        for(MDeployDao mDeployDao: list){
            connectToClient.deleteInstance(mDeployDao.getServiceName());
            MDatabaseUtils.databaseUtils.deleteDeployById(mDeployDao.getPodId());
        }
        // 撤销版本的演化
        Deletestrategy.Revocationversion(serviceName, serviceVersion);
    }


    @PostMapping("/byNameAndVersion1")
    public void deleteAllServiceAndversion(@RequestParam("serviceName")String name, @RequestParam("serviceVersion")String version){
        version = MSvcVersion.fromStr(version).toString();
        List<MDeployDao> mDeployDaos = MDatabaseUtils.databaseUtils.getDeployByserviceId(name, version);
        for(MDeployDao mDeployDao:mDeployDaos){
            connectToClient.deleteInstance(mDeployDao.getPodId());
            MDatabaseUtils.databaseUtils.deleteDeployById(mDeployDao.getPodId());
        }
    }

    @PostMapping("/byName")
    public void deleteAllServiceAndversion(@RequestParam("serviceName")String name){
        List<MDeployDao> mDeployDaos = MDatabaseUtils.databaseUtils.getDeployByserviceName(name);
        for(MDeployDao mDeployDao:mDeployDaos){
            connectToClient.deleteInstance(mDeployDao.getPodId());
            MDatabaseUtils.databaseUtils.deleteDeployById(mDeployDao.getPodId());
        }
    }


    @PostMapping("/deleteById")
    public void byId(@RequestParam("id")String id){
        connectToClient.deleteInstance(id);
        MDatabaseUtils.databaseUtils.deleteDeployById(id);
    }

}
