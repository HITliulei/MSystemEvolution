package com.septemberhx.runenvagent.controller;

import com.septemberhx.common.bean.agent.MDeployPodRequest;
import com.septemberhx.common.bean.agent.MInstanceInfoResponse;
import com.septemberhx.runenvagent.adaptor.RunEnvAdaptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author Lei
 * @Date 2020/7/20 17:10
 * @Version 1.0
 */

@RestController
@RequestMapping("mk8smagent")
public class MRunEnvController {

    private static Logger logger = LogManager.getLogger(MRunEnvController.class);

    @Autowired
    @Qualifier("RunEnvAdaptorImpl")
    private RunEnvAdaptor runEnvAdaptor;


    /**
     * deploy one instance in cluster(add)
     * @param mDeployPodRequest information about the microservice instance to be deployed
     */
    @PostMapping(path = "/deploy")
    public void deploy(@RequestBody MDeployPodRequest mDeployPodRequest) {
        logger.info("info of service to be deployed: " + mDeployPodRequest);
        this.runEnvAdaptor.deployInstanceOnNode(mDeployPodRequest);

    }

    /**
     * delete the instance(delete)
     * @param instanceId the id of instance to be deleted
     */
    @DeleteMapping(path = "/deleteInstance")
    public void deleteInstance(@RequestParam("dockerInstanceId") String instanceId) {
        logger.info("instanceId to be depleted: " + instanceId);
        this.runEnvAdaptor.deleteInstanceById(instanceId);
    }


    /**
     * get all node and its label
     * @return node , node-label
     */
    @GetMapping("/getAllNodeLable")
    public Map<String,String> getallNodeLabel(){
        return this.runEnvAdaptor.getAllnode();
    }

    /**
     * get all instances in cluster(get)
     * @return all instances in cluster
     */
    @ResponseBody
    @RequestMapping(path = "/instanceInfoList", method = RequestMethod.GET)
    public MInstanceInfoResponse getInstanceInfoList() {
        return null;
    }

}
