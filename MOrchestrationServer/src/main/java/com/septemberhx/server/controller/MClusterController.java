package com.septemberhx.server.controller;

import com.septemberhx.common.bean.agent.MInstanceInfoBean;
import com.septemberhx.server.dao.MDeployDao;
import com.septemberhx.server.utils.MDatabaseUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/1/10
 *
 * The controller used for accepting system information like edge servers and so on.
 */

@RestController
@RequestMapping(value = "/cluster")
public class MClusterController {

    private static Logger logger = LogManager.getLogger(MClusterController.class);

    @RequestMapping(path = "/reportInstanceInfo", method = RequestMethod.POST)
    public void loadInstanceInfo(@RequestBody MInstanceInfoBean instanceInfo) {
        logger.info("有注册中心返回，并经过Cluster Agent处理得到的微服务" + instanceInfo);
        MDeployDao mDeployDao = MDatabaseUtils.databaseUtils.getdeployInfo(instanceInfo.getDockerInfo().getInstanceId());
        MDeployDao mDeployDao1 = new MDeployDao(mDeployDao.getPodId(), instanceInfo.getRegistryId(), mDeployDao.getNodeId(), mDeployDao.getServiceName(), mDeployDao.getServiceVersion(), instanceInfo.getIp());
        MDatabaseUtils.databaseUtils.updateRegister(mDeployDao1);
//        List<MDeployDao> list = MDatabaseUtils.databaseUtils.getAlldeployInfo();
//        for(MDeployDao mDeployDao: list){
//            System.out.println("对比mDeploy" + mDeployDao.getPodId() + " :  InstanceId :  " + instanceInfo.getDockerInfo().getInstanceId());
//            if(mDeployDao.getPodId().equalsIgnoreCase(instanceInfo.getDockerInfo().getInstanceId())
//                    && mDeployDao.getNodeId().equalsIgnoreCase(instanceInfo.getDockerInfo().getNodeLabel())
//                    && mDeployDao.getServiceName().equalsIgnoreCase(instanceInfo.getServiceName())){
//                mDeployDao.setRegisterId(instanceInfo.getRegistryId());
//                mDeployDao.setIpAddress(instanceInfo.getIp());
//                MDatabaseUtils.databaseUtils.updateRegister(mDeployDao);
//                break;
//            }
//        }
    }
}
