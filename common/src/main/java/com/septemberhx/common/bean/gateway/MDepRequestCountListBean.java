package com.septemberhx.common.bean.gateway;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/4/12
 */
@Getter
@Setter
@ToString
public class MDepRequestCountListBean {
    private List<MDepRequestCountBean> countList;

    public MDepRequestCountListBean(List<MDepRequestCountBean> countList) {
        this.countList = countList;
    }

    public MDepRequestCountListBean() {
    }
}
