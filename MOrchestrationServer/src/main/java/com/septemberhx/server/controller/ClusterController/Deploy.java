package com.septemberhx.server.controller.ClusterController;

import com.septemberhx.common.bean.agent.MDeployPodRequest;
import com.septemberhx.common.bean.instance.*;
import com.septemberhx.common.service.MSvcVersion;
import com.septemberhx.server.algorithm.deploy.Deploystrategy;
import com.septemberhx.server.client.ConnectToClient;
import com.septemberhx.server.dao.MDeployDao;
import com.septemberhx.server.utils.MDatabaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @Author Lei
 * @Date 2020/3/16 14:16
 * @Version 1.0
 */

@RestController
@RequestMapping("/deploy")
public class Deploy {


    @Autowired
    private ConnectToClient connectToClient;

    @PostMapping("/deployOneInstanceOnNode")
    public void deploy(@RequestBody MDeployVersion mDeployVersion){
        System.out.println("部署信息为:" + mDeployVersion);
        Deploystrategy.deployOneInstanceOnNode(mDeployVersion, connectToClient);
    }

    @PostMapping("deployInstanceWithoutNode")
    public void deployWithoutNode(@RequestBody MDeployVerionWithoutNode mDeployVerionWithoutNode){
        Deploystrategy.deployServiceVersion(mDeployVerionWithoutNode, connectToClient);
    }


    @PostMapping("/OnlydeployOneInstanceOnNode")
    public void Onldeploy(@RequestBody MDeployVersion mDeployVersion){
        System.out.println("简单部署");
        String serviceId = mDeployVersion.getServiceName() + "_"+ MSvcVersion.fromStr(mDeployVersion.getServiceVersion()).toString();
        String uniteid = UUID.randomUUID().toString().substring(24);
        String imageurl = MDatabaseUtils.databaseUtils.getServiceById(serviceId).getImageUrl();
        MDeployPodRequest mDeployPodRequest = new MDeployPodRequest(serviceId, mDeployVersion.getNodeid(), uniteid, mDeployVersion.getServiceName().toLowerCase(), imageurl);
        connectToClient.deploy(mDeployPodRequest);
        MDatabaseUtils.databaseUtils.deploy(new MDeployDao(uniteid, mDeployVersion.getNodeid(), mDeployVersion.getServiceName().toLowerCase(), MSvcVersion.fromStr(mDeployVersion.getServiceVersion()).toString()));
        connectToClient.register(mDeployPodRequest);
    }


    public static void main(String[] args) {
        System.out.println(UUID.randomUUID().toString().substring(24));
    }



}
