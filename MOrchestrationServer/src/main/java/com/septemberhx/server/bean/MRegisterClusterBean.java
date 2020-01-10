package com.septemberhx.server.bean;

import com.septemberhx.common.base.node.MServerCluster;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/1/10
 */
@Getter
@Setter
@ToString
public class MRegisterClusterBean {
    private MServerCluster serverCluster;
    private List<MConnectionJson> connectionList;
}
