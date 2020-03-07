package com.septemberhx.server.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/1/1
 */
@Getter
@Setter
public class MSystemModel {
    private MSvcManager serviceManager;
    private MJobManager jobManager;
    private MClusterManager nodeManager;
    private MSvcInstManager instanceManager;
    private MSvcDepManager svcDepManager;

    private static Logger logger = LogManager.getLogger(MSystemModel.class);

    public MSystemModel() {
        // todo: init service manager from the database
        this.serviceManager = new MSvcManager();
        this.jobManager = new MJobManager();
        this.nodeManager = new MClusterManager();
        this.instanceManager = new MSvcInstManager();
    }
}
