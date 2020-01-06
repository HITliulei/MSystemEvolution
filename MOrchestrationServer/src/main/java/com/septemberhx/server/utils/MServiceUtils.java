package com.septemberhx.server.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/1/1
 *
 * Fetch all the variables and so on
 */
@Component
public class MServiceUtils {

    public static String BUILD_CENTER_IP;
    public static Integer BUILD_CENTER_PORT;

    @Value("${msystemevolution.orchestration.buildCenter.ip}")
    public void setBuildCenterIp(String buildCenterIp) {
        BUILD_CENTER_IP = buildCenterIp;
    }

    @Value("${msystemevolution.orchestration.buildCenter.port}")
    public void setBuildCenterPort(Integer buildCenterPort) {
        BUILD_CENTER_PORT = buildCenterPort;
    }
}
