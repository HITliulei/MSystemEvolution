package com.septemberhx.server.model;

import com.septemberhx.common.base.node.MServerNode;
import com.septemberhx.common.bean.agent.MInstanceInfoBean;
import com.septemberhx.common.service.MService;
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
public class MServerSkeleton {

    @Getter
    @Setter
    private MSystemModel currSystemModel;       // record current info about the system

    @Getter
    @Setter
    private MSystemModel nextSystemModel;       // record the system info after evolution

    private static MServerSkeleton instance;
    private static Logger logger = LogManager.getLogger(MServerSkeleton.class);

    private MServerSkeleton() {
        this.currSystemModel = new MSystemModel();
    }

    public static MServerSkeleton getInstance() {
        if (instance == null) {
            synchronized (MServerSkeleton.class) {
                instance = new MServerSkeleton();
            }
        }
        return instance;
    }

    public static MSvcManager getCurrSvcManager() {
        return MServerSkeleton.getInstance().getCurrSystemModel().getServiceManager();
    }

    public static MSvcManager getNextSvcManager() {
        return MServerSkeleton.getInstance().getNextSystemModel().getServiceManager();
    }

    public static MClusterManager getCurrNodeManager() {
        return MServerSkeleton.getInstance().getCurrSystemModel().getNodeManager();
    }
}
