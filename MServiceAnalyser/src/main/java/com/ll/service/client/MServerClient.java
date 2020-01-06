package com.ll.service.client;

import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.bean.server.MServiceAnalyzeResultBean;
import com.septemberhx.common.config.MClusterConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/1/6
 */
@FeignClient(value = MClusterConfig.SERVICE_NAME_SERVER)
public interface MServerClient {
    @PostMapping(value = MClusterConfig.SERVER_SERVICE_INFO_CALLBACK_URI)
    MResponse pushServiceInfos(@RequestBody MServiceAnalyzeResultBean resultBean);
}
