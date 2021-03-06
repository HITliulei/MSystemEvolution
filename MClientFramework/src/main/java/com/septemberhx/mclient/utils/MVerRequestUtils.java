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

    public static MResponse request(String requestId, MResponse parameters, RequestMethod requestMethod) {
        // Send the request dependence to the gateway in order to determine which instance should be requested
        parameters.set(MConfig.MGATEWAY_DEPENDENCY_ID, MClientSkeleton.inst().getDepListById(requestId).get(0));

        // Get an online MGateway instance randomly, and send the request to it
        InstanceInfo gatewayInstance = MClientSkeleton.inst().getRandomServiceInstance(MConfig.MGATEWAY_NAME);
        MResponse response = null;
        if (gatewayInstance != null) {
            response = MRequestUtils.sendRequest(
                    MUrlUtils.getRemoteUri(gatewayInstance.getIPAddr(),
                            gatewayInstance.getPort(),
                            MConfig.MGATEWAY_DEPENDENCY_CALL),
                    parameters,
                    MResponse.class,
                    requestMethod
            );
        }
        return response;
    }
}
