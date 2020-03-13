package com.ll.mbuildcenter.client;

import com.septemberhx.common.bean.server.MBuildJobFinishedBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author Lei
 * @Date 2020/2/14 16:12
 * @Version 1.0
 */

@FeignClient(name = "MOrchestrationServer")
public interface CenterClient {

    @RequestMapping("/job/buildNotify")
    public void returnResult(@RequestBody MBuildJobFinishedBean mBuildJobFinishedBean);
}
