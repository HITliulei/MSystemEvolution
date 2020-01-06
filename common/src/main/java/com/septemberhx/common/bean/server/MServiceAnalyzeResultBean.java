package com.septemberhx.common.bean.server;

import com.septemberhx.common.service.MService;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/1/6
 */
@Getter
@Setter
@ToString
public class MServiceAnalyzeResultBean {
    private List<MService> serviceList;

    public MServiceAnalyzeResultBean(List<MService> serviceList) {
        this.serviceList = serviceList;
    }

    public MServiceAnalyzeResultBean() {
    }
}
