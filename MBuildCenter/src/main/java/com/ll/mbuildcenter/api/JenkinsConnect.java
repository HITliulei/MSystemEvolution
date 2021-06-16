package com.ll.mbuildcenter.api;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @Author Lei
 * @Date 2020/2/13 22:48
 * @Version 1.0
 */


public class JenkinsConnect {
    private JenkinsConnect(){}
    static final String JENKINS_URL = "http://54.65.128.130:8080/";
    static final String JENKINS_USERNAME = "root";
    static final String JENKINS_PASSWORD = "root";

    public static JenkinsHttpClient getClient(){
        JenkinsHttpClient jenkinsHttpClient = null;
        try {
            jenkinsHttpClient = new JenkinsHttpClient(new URI(JENKINS_URL), JENKINS_USERNAME, JENKINS_PASSWORD);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return jenkinsHttpClient;
    }


    public static JenkinsServer connection() {
        JenkinsServer jenkinsServer = null;
        try {
            jenkinsServer = new JenkinsServer(new URI(JENKINS_URL), JENKINS_USERNAME, JENKINS_PASSWORD);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return jenkinsServer;
    }
}
