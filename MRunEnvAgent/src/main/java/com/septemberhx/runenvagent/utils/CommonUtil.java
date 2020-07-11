package com.septemberhx.runenvagent.utils;

import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.util.Yaml;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;


/**
 * @Author Lei
 * @Date 2020/7/11 22:35
 * @Version 1.0
 */

public class CommonUtil {
    private static Logger logger = LogManager.getLogger(CommonUtil.class);

    public static V1Pod readPodYaml(String serviceName) {
        V1Pod pod = null;
        try {
            Object podYamlObj = Yaml.load(new File("./yaml/" + serviceName + ".yaml"));
            if (podYamlObj instanceof V1Pod) {
                pod = (V1Pod) podYamlObj;
            }
        } catch (Exception e) {
            logger.info(e);
        }
        return pod;
    }
}
