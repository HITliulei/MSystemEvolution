package com.ll.mbuildcenter.controller;

import com.ll.mbuildcenter.client.CenterClient;
import com.septemberhx.common.bean.server.MBuildJobBean;
import com.septemberhx.common.bean.server.MBuildJobFinishedBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author Lei
 * @Date 2020/2/13 22:37
 * @Version 1.0
 */

@RestController
@RequestMapping("/buildcenter")
public class BuildCenter {

    @Autowired
    private CenterClient centerClient;
    private static ExecutorService executorService = Executors.newFixedThreadPool(8);

    @RequestMapping("/build")
    public void buildJob(@RequestBody MBuildJobBean mBuildJobBean){
        String jobName = mBuildJobBean.getServiceName()+"_"+mBuildJobBean.getGitTag();
        JobApi.createJob(mBuildJobBean,jobName);
        executorService.submit(() -> {
            String result = JobApi.buildJob(jobName);
            if(result.equals("SUCCESS")){
                MBuildJobFinishedBean mBuildJobFinishedBean = new MBuildJobFinishedBean();
                mBuildJobFinishedBean.setId(jobName);
                mBuildJobFinishedBean.setImageUrl("micheallei/"+mBuildJobBean.getServiceName()+":"+mBuildJobBean.getGitTag());
                mBuildJobFinishedBean.setSuccess(true);
                centerClient.returnResult(mBuildJobFinishedBean);
            }else{
                centerClient.returnResult(new MBuildJobFinishedBean());
            }
        });
    }
}
