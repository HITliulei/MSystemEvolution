package com.septemberhx.svcenvagent.adaptor;

import com.septemberhx.common.bean.agent.MInstanceRegisterNotifyRequest;
import com.septemberhx.common.bean.svcenvagent.MSvcEnvInfoResponse;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/7/11
 */
public interface ISvcEnvAdaptor {
    /*
     * Get all the service instance infos in the cluster
     */
    MSvcEnvInfoResponse getInstanceInfoList();

    /*
     * Accept the instance status change information from the registry and notify it to up layer
     */
    void instanceRegistered(@RequestBody MInstanceRegisterNotifyRequest registerNotifyRequest);
}
