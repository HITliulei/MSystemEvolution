package com.septemberhx.common.bean.gateway;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/11
 */
@Getter
@Setter
public class MDepRequestCacheListBean {
    private List<MDepRequestCacheBean> requestList;

    public MDepRequestCacheListBean(List<MDepRequestCacheBean> requestList) {
        this.requestList = requestList;
    }

    public MDepRequestCacheListBean() {
    }
}
