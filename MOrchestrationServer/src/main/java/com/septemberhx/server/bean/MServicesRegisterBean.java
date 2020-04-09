package com.septemberhx.server.bean;

import com.septemberhx.common.bean.server.MServiceRegisterBean;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/4/10
 */
@Getter
@Setter
@ToString
public class MServicesRegisterBean {
    private List<MServiceRegisterBean> services;
}
