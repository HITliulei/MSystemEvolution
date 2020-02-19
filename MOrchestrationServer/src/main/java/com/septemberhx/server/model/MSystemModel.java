package com.septemberhx.server.model;

import com.septemberhx.common.base.node.MServerNode;
import com.septemberhx.common.bean.agent.MInstanceInfoBean;
import com.septemberhx.common.service.MService;
import com.septemberhx.server.utils.MIDUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

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
    private MClusterManager nodeManager;
    private MServiceInstanceManager instanceManager;

    private static Logger logger = LogManager.getLogger(MSystemModel.class);

    public MSystemModel() {
        // todo: init service manager from the database
        this.serviceManager = new MServiceManager();
        this.jobManager = new MJobManager();
        this.nodeManager = new MClusterManager();
        this.instanceManager = new MServiceInstanceManager();
    }
}
