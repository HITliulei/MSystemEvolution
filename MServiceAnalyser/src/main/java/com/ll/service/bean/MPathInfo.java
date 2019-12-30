package com.ll.service.bean;

import java.util.List;

/**
 * Created by Lei on 2019/11/29 15:53
 */
public class MPathInfo {

    private String application_Path;
    private List<String> controller_ListPath;

    public MPathInfo(){

    }
    public MPathInfo(String application_Path, List<String> controller_ListPath) {
        this.application_Path = application_Path;
        this.controller_ListPath = controller_ListPath;
    }

    public String getApplication_Path() {
        return application_Path;
    }

    public void setApplication_Path(String application_Path) {
        this.application_Path = application_Path;
    }

    public List<String> getController_ListPath() {
        return controller_ListPath;
    }

    public void setController_ListPath(List<String> controller_ListPath) {
        this.controller_ListPath = controller_ListPath;
    }

    @Override
    public String toString() {
        return "MPathInfo{" +
                "application_Path='" + application_Path + '\'' +
                ", controller_ListPath=" + controller_ListPath +
                '}';
    }
}
