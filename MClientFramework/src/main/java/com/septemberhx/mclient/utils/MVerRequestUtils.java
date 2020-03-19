package com.septemberhx.mclient.utils;

import com.netflix.appinfo.InstanceInfo;
import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.config.MConfig;
import com.septemberhx.common.utils.MRequestUtils;
import com.septemberhx.common.utils.MUrlUtils;
import com.septemberhx.mclient.core.MClientSkeleton;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/1
 */
public class MVerRequestUtils {

    public static MResponse request(String requestId, MResponse parameters, RequestMethod requestMethod, HttpServletRequest request) {
        // Send the request dependence to the gateway in order to determine which instance should be requested
        parameters.set(MConfig.MGATEWAY_DEPENDENCY_ID, MClientSkeleton.inst().getDepListById(requestId).get(0));
        String calledUrl = request.getHeader(MConfig.PARAM_CALLED_URL);
        String callerUrl = request.getHeader(MConfig.PARAM_CALLER_URL);
        Map<String, List<String>> customHeaders = new HashMap<>();
        List<String> p1 = new ArrayList<>();
        p1.add(callerUrl);
        List<String> p2 = new ArrayList<>();
        p2.add(calledUrl);
        customHeaders.put(MConfig.PARAM_CALLER_URL, p1);
        customHeaders.put(MConfig.PARAM_CALLED_URL, p2);

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
                    requestMethod,
                    customHeaders
            );
        }
        return response;
    }
}
