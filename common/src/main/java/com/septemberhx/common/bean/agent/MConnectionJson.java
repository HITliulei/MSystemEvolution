package com.septemberhx.common.bean.agent;

import com.septemberhx.common.base.node.MNodeConnectionInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2019/11/24
 */
@Getter
@Setter
public class MConnectionJson {
    private String successor;
    private String predecessor;
    private MNodeConnectionInfo connection;
}