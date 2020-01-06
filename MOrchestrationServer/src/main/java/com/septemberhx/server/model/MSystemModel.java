package com.septemberhx.server.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/1/1
 */
@Getter
@Setter
public class MSystemModel {
    private MServiceManager serviceManager;
    private MJobManager jobManager;

    public MSystemModel() {
        // todo: init service manager from the database
        this.serviceManager = new MServiceManager();
        this.jobManager = new MJobManager();
    }
}
