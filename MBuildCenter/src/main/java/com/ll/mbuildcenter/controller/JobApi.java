package com.ll.mbuildcenter.controller;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.*;
import com.septemberhx.common.bean.server.MBuildJobBean;
import com.septemberhx.common.build.XmlTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author Lei
 * @Date 2020/2/13 22:49
 * @Version 1.0
 */


public class JobApi {

    private static Logger logger = LogManager.getLogger(JobApi.class);

    /**
     * 创建 Job
     */
    public static void createJob(MBuildJobBean mBuildJobBean, String jobName){
        String[] gits = mBuildJobBean.getGitUrl().split("/");
        String projectName = gits[gits.length-1].split("\\.")[0];
        JenkinsServer jenkinsServer = JenkinsConnect.connection();
        try {
            String xml = XmlTemplate.getXml(mBuildJobBean.getServiceName(), mBuildJobBean.getGitTag(), projectName, mBuildJobBean.getGitUrl());
            if(jenkinsServer.getJobs().keySet().contains(jobName)){
                jenkinsServer.updateJob(jobName,xml);
            }else{
                jenkinsServer.createJob(jobName,xml);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
    * 更新job
    */
    public static void updateJob(String job,String xml){
        JenkinsServer jenkinsServer = JenkinsConnect.connection();
        try{
            jenkinsServer.updateJob(job,xml);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 执行无参数 Job build
     */

    public static String buildJob(String job){
        JenkinsServer jenkinsServer = JenkinsConnect.connection();
        try {
            Job j = jenkinsServer.getJob(job);
            j.build();
            /*构建状态*/
            TimeUnit.SECONDS.sleep(5);
            /*等待最后以此构建*/
            long a = 0;
            int buildnumber = j.details().getNextBuildNumber();
            int completeBuildNumber = j.details().getLastCompletedBuild().getNumber();
            logger.info(job +"的第"+buildnumber+"构建");
            while (buildnumber != completeBuildNumber && a <= 600){
                TimeUnit.SECONDS.sleep(5);
                a = a + 5;
                logger.info("构建的第" + a  +"s");
                completeBuildNumber = j.details().getLastCompletedBuild().getNumber();
            }
            if(buildnumber != completeBuildNumber){
                j.details().getLastBuild().Stop();   /*停止构建*/
                return "Fail  To Build";
            }
            return jenkinsServer.getJob(job).details().getLastCompletedBuild().details().getResult().toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Fail To Build";
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "Fail To Build";
        }
    }

    public static Job getJob(String jobName){
        JenkinsServer jenkinsServer = JenkinsConnect.connection();
        try{
            return jenkinsServer.getJob(jobName);
        }catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取 Job 基本信息
     */
//    public void getJob(String jobNmae){
//        try {
//            JobWithDetails job = jenkinsServer.getJob(jobNmae);
//            System.out.println(job.getName());
//            System.out.println(job.getUrl());
//            // 获取 Job 下一个 build 编号
//            System.out.println(job.getNextBuildNumber());
//            // 获取 Job 显示的名称
//            System.out.println(job.getDisplayName());
//            // 输出 Job 描述信息
//            System.out.println(job.getDescription());
//            // 获取 Job 下游任务列表
//            System.out.println(job.getDownstreamProjects());
//            // 获取 Job 上游任务列表
//            System.out.println(job.getUpstreamProjects());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 获取 Job 列表
     */
    public static Map<String,Job> getJobList(){
        JenkinsServer jenkinsServer = JenkinsConnect.connection();
        try {
            // 获取 Job 列表
            Map<String,Job> jobs = jenkinsServer.getJobs();
            return jobs;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 查看 Job XML 信息
     */
    public static void getJobConfig(String job){
        JenkinsServer jenkinsServer = JenkinsConnect.connection();
        try {
            String xml = jenkinsServer.getJobXml(job);
            System.out.println(xml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * 停止最后构建的 Job Build
     */
    public static void stopLastJobBuild(){
        JenkinsServer jenkinsServer = JenkinsConnect.connection();
        try {
            // 获取最后的 build 信息
            Build build = jenkinsServer.getJob("test-job").getLastBuild();
            // 停止最后的 build
            build.Stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除 Job
     */
    public static void deleteJob(String jobName){
        JenkinsServer jenkinsServer = JenkinsConnect.connection();
        try {
            jenkinsServer.deleteJob(jobName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
