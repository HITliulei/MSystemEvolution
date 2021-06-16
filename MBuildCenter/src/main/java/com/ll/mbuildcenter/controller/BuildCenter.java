package com.ll.mbuildcenter.controller;

import com.ll.mbuildcenter.api.JobApi;
import com.ll.mbuildcenter.client.CenterClient;
import com.ll.mbuildcenter.utils.MGatewayRequest;
import com.septemberhx.common.bean.server.MBuildJobBean;
import com.septemberhx.common.bean.server.MBuildJobFinishedBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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
                mBuildJobFinishedBean.setImageUrl("micheallei/"+mBuildJobBean.getServiceName().toLowerCase()+":"+mBuildJobBean.getGitTag());
                mBuildJobFinishedBean.setSuccess(true);
                centerClient.returnResult(mBuildJobFinishedBean);
//                new MGatewayRequest().sendRequest("http://54.65.128.130:58080/job/buildNotify",null, RequestMethod.POST,mBuildJobFinishedBean);

            }else{
                centerClient.returnResult(new MBuildJobFinishedBean());
//                new MGatewayRequest().sendRequest("http://54.65.128.130:58080/job/buildNotify",null, RequestMethod.POST, new MBuildJobFinishedBean());
            }
        });
    }

    @RequestMapping("/test")
    public void test(@RequestBody MBuildJobBean mBuildJobBean){
        String jobName = mBuildJobBean.getServiceName()+"_"+mBuildJobBean.getGitTag();
        JobApi.createJob(mBuildJobBean,jobName);
        String result = JobApi.buildJob(jobName);
    }
    public static void main(String[] args) {
        MBuildJobBean mBuildJobBean = new MBuildJobBean();
        mBuildJobBean.setId("MAliPay_v2.3.1");
        mBuildJobBean.setServiceName("MAliPay");
        mBuildJobBean.setGitUrl("http://10.111.1.104:12345/BoyLei/malipay.git");
        mBuildJobBean.setGitTag("v2.3.1");
        String jobName = mBuildJobBean.getServiceName()+"_"+mBuildJobBean.getGitTag();
        JobApi.createJob(mBuildJobBean,jobName);
        String result = JobApi.buildJob(jobName);
    }
}
