package com.ll.service.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Created by Lei on 2019/11/29 15:53
 */

@Getter
@Setter
public class MPathInfo {

    private String gitUrl;

    private String applicationPath;

    private List<String> controllerListPath;

    @Override
    public String toString() {
        return "MPathInfo{" +
                "application_Path='" + applicationPath + '\'' +
                ", controller_ListPath=" + controllerListPath +
                '}';
    }
}
