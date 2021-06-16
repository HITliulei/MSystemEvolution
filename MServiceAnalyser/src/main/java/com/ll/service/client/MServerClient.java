package com.ll.service.client;

import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.bean.server.MServiceAnalyzeResultBean;
import com.septemberhx.common.config.MConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/1/6
 */
@FeignClient(name = MConfig.SERVICE_NAME_SERVER)
public interface MServerClient {
    @RequestMapping(value = MConfig.SERVER_SERVICE_INFO_CALLBACK_URI, method = RequestMethod.POST)
    MResponse pushServiceInfos(@RequestBody MServiceAnalyzeResultBean resultBean);
}
