package com.septemberhx.runenvagent.controller;

import com.septemberhx.common.bean.agent.MDeployPodRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

/**
 * @Author Lei
 * @Date 2020/7/11 21:38
 * @Version 1.0
 */
@RestController
@EnableAutoConfiguration
@RequestMapping("/magent")
public class MAgentController {
    private static Logger logger = LogManager.getLogger(MAgentController.class);

    /**
     * deploy one instance in cluster
     * @param mDeployPodRequest information about the microservice instance to be deployed
     */
    @RequestMapping(path = "/deploy", method = RequestMethod.POST)
    public void deploy(@RequestBody MDeployPodRequest mDeployPodRequest) {
        logger.info("info of service to be deployed: " + mDeployPodRequest);
    }

    /**
     * delete the instance
     * @param instanceId the id of instance to be deleted
     */
    @RequestMapping(path = "/deleteInstance", method = RequestMethod.GET)
    public void deleteInstance(@RequestParam("dockerInstanceId") String instanceId) {
        logger.info("instanceId to be depleted: " + instanceId);
    }
}
