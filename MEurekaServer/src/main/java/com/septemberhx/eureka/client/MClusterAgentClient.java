package com.septemberhx.eureka.client;

import com.septemberhx.common.bean.agent.MInstanceRegisterNotifyRequest;
import com.septemberhx.common.config.MConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = MConfig.MCLUSTERAGENT_NAME)
public interface MClusterAgentClient {
    @RequestMapping(value = MConfig.MCLUSTERAGENT_INSTANCE_REGISTER_URL, method = RequestMethod.POST)
    void instanceRegistered(@RequestBody MInstanceRegisterNotifyRequest registerNotifyRequest);
}
