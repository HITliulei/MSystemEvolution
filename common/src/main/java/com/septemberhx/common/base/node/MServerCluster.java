package com.septemberhx.common.base.node;

import com.septemberhx.common.base.MUniqueObject;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Map;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/1/10
 *
 * Server Cluster
 */
@Getter
@Setter
public class MServerCluster extends MUniqueObject {

    /*
     * The Ip and Port of the MClusterAgent
     * We assume the ip:port is load balanced
     */
    private String clusterAgentIp;
    private Integer clusterAgentPort;

    /*
     * The Ip and Port of the MGateway
     * We assume the ip:port is load balanced
     */
    private String clusterGatewayIp;
    private String clusterGatewayPort;

    /*
     * Server node map
     */
    private Map<String, MServerNode> nodeMap;

    public Collection<? extends MServerNode> allNodes() {
        return this.nodeMap.values();
    }
}
