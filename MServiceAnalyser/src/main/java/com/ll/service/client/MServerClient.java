package com.ll.service.client;

import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.bean.server.MServiceAnalyzeResultBean;
import com.septemberhx.common.config.MConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/1/6
 */
@FeignClient(value = MConfig.SERVICE_NAME_SERVER)
public interface MServerClient {
    @PostMapping(value = MConfig.SERVER_SERVICE_INFO_CALLBACK_URI)
    @ResponseBody
    MResponse pushServiceInfos(@RequestBody MServiceAnalyzeResultBean resultBean);
}
