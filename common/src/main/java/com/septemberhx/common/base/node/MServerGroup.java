package com.septemberhx.common.base.node;

import com.septemberhx.common.base.MUniqueObject;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/7/6
 *
 * Smallest regional autonomous unit in the system (if no group presents, cluster is the smallest one)
 */
@Setter
@Getter
public class MServerGroup extends MUniqueObject {

    /*
     * Proxy agent for the whole group for evolution operations
     */
    private String groupAgentIp;
    private String groupAgentPort;

    /*
     * Cluster Id it belongs to
     */
    private String clusterId;

    /*
     * Server node map
     */
    private Map<String, MServerNode> nodeMap;
}
