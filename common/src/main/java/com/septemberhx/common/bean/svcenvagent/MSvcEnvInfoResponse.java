package com.septemberhx.common.bean.svcenvagent;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/7/11
 */
@Getter
@Setter
@ToString
public class MSvcEnvInfoResponse {
    private List<MSvcEnvInfoBean> infoList;
}
