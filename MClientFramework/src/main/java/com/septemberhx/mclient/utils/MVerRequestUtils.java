package com.septemberhx.mclient.utils;

import com.netflix.appinfo.InstanceInfo;
import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.config.MConfig;
import com.septemberhx.common.utils.MRequestUtils;
import com.septemberhx.common.utils.MUrlUtils;
import com.septemberhx.mclient.core.MClientSkeleton;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/1
 */
public class MVerRequestUtils {

    public MResponse request(String requestId, MResponse parameters, RequestMethod requestMethod) {
        parameters.set(MConfig.MGATEWAY_DEPENDENCY_ID, requestId);
        InstanceInfo gatewayInstance = MClientSkeleton.inst().getRandomServiceInstance(MConfig.MGATEWAY_NAME);
        MResponse response = null;
        if (gatewayInstance != null) {
            response = MRequestUtils.sendRequest(
                    MUrlUtils.getRemoteUri(gatewayInstance.getIPAddr(),
                            gatewayInstance.getPort(),
                            MConfig.MGATEWAY_DEPENDENCY_CALL),
                    MResponse.class,
                    MResponse.class,
                    requestMethod
            );
        }
        return response;
    }
}
