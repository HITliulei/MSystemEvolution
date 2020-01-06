package com.ll.service.bean;

import java.util.List;

/**
 * Created by Lei on 2019/11/29 15:53
 */
public class MPathInfo {

    private String applicationPath;
    private List<String> controllerListPath;

    public MPathInfo(){

    }
    public MPathInfo(String applicationPath, List<String> controllerListPath) {
        this.applicationPath = applicationPath;
        this.controllerListPath = controllerListPath;
    }

    public String getApplicationPath() {
        return applicationPath;
    }

    public void setApplicationPath(String applicationPath) {
        this.applicationPath = applicationPath;
    }

    public List<String> getControllerListPath() {
        return controllerListPath;
    }

    public void setControllerListPath(List<String> controllerListPath) {
        this.controllerListPath = controllerListPath;
    }

    @Override
    public String toString() {
        return "MPathInfo{" +
                "application_Path='" + applicationPath + '\'' +
                ", controller_ListPath=" + controllerListPath +
                '}';
    }
}
