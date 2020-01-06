package com.septemberhx.server.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/1/1
 */
public class MServerSkeleton {

    @Getter
    @Setter
    private MSystemModel currSystemModel;

    private static volatile MServerSkeleton instance;

    private MServerSkeleton() { }

    public static MServerSkeleton getInstance() {
        if (instance == null) {
            synchronized (MServerSkeleton.class) {
                if (instance == null) {
                    instance = new MServerSkeleton();
                }
            }
        }
        return instance;
    }

    public static MServiceManager getCurrSvcManager() {
        return MServerSkeleton.getInstance().getCurrSystemModel().getServiceManager();
    }

    public static MJobManager getCurrJobManager() {
        return MServerSkeleton.getInstance().getCurrSystemModel().getJobManager();
    }
}
