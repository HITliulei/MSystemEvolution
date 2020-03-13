package com.septemberhx.server.client;

import com.septemberhx.common.bean.server.MBuildJobBean;
import com.septemberhx.server.job.MBuildJob;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author Lei
 * @Date 2020/3/12 17:09
 * @Version 1.0
 */

@FeignClient(name = "MBuildCenter")
public interface MBuildCenterClient {

    @RequestMapping("/buildcenter/build")
    public void build(@RequestBody MBuildJobBean buildInfo);
}
