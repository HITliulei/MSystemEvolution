package com.septemberhx.server.model;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import com.septemberhx.common.base.node.MNodeConnectionInfo;
import com.septemberhx.common.base.node.MServerCluster;
import com.septemberhx.common.base.node.MServerNode;
import com.septemberhx.common.base.MUniqueObjectManager;

import java.util.*;
import java.util.stream.Collectors;


/**
 * We assume that all server nodes in one cluster can connect to each other.
 * The server nodes in the same cluster differs
 * The server nodes in different clusters may be the same
 */
public class MClusterManager extends MUniqueObjectManager<MServerCluster> {
    private MutableValueGraph<String, MNodeConnectionInfo> serverNodeGraph;
    private Map<String, MServerNode> nodeMap;

    public void reset() {
        this.objectMap.clear();
        this.serverNodeGraph = ValueGraphBuilder.directed().allowsSelfLoops(true).build();
        this.nodeMap.clear();
    }

    public MClusterManager() {
        this.serverNodeGraph = ValueGraphBuilder.directed().allowsSelfLoops(true).build();
        this.nodeMap = new HashMap<>();
    }

    public void addConnectionInfo(MNodeConnectionInfo info, String startNodeId, String endNodeId) {
        serverNodeGraph.putEdgeValue(startNodeId, endNodeId, info);
    }

    public void removeConnectionInfo(String startNodeId, String endNodeId) {
        this.serverNodeGraph.removeEdge(startNodeId, endNodeId);
    }

    public void add(MServerCluster cluster) {
        this.objectMap.put(cluster.getId(), cluster);
        for (MServerNode serverNode : cluster.getNodeMap().values()) {
            this.serverNodeGraph.addNode(serverNode.getId());
            this.nodeMap.put(serverNode.getId(), serverNode);
        }
    }

    public void remove(String clusterId) {
        Optional<MServerCluster> clusterOptional = this.getById(clusterId);
        if (clusterOptional.isPresent()) {
            for (MServerNode serverNode : clusterOptional.get().getNodeMap().values()) {
                // remove the node in node map
                this.nodeMap.remove(serverNode.getId());

                // remove the node in the graph
                this.serverNodeGraph.removeNode(serverNode.getId());

                // remove the connection info of the given node
                for (EndpointPair<String> edge : this.serverNodeGraph.incidentEdges(serverNode.getId())) {
                    this.serverNodeGraph.removeEdge(edge);
                }
            }
            // remove the cluster info
            this.objectMap.remove(clusterId);
        }
    }

    /**
     * Get other nodes that the delay between it and given node is less than MAX_DELAY_TOLERANCE
     * The result will be ordered by the delay in decent
     * @param serverNodeId: given node id
     * @return server node list
     */
    public List<MServerNode> getConnectedNodesDecentWithDelayTolerance(String serverNodeId) {
        List<EndpointPair<String>> edgeList = new ArrayList<>(this.serverNodeGraph.incidentEdges(serverNodeId));
        List<MServerNode> successorList = new ArrayList<>();
        for (EndpointPair<String> edge : edgeList) {
            if (!edge.nodeV().equals(serverNodeId)) {
                successorList.add(this.nodeMap.get(edge.nodeV()));
            }
        }
        Collections.sort(successorList, (o1, o2) -> {
            Optional<MNodeConnectionInfo> cIOption1 = serverNodeGraph.edgeValue(serverNodeId, o1.getId());
            Optional<MNodeConnectionInfo> cIOption2 = serverNodeGraph.edgeValue(serverNodeId, o2.getId());
            if (cIOption1.isPresent() && cIOption2.isPresent()) {
                return cIOption1.get().compareTo(cIOption2.get());
            } else {
                if (cIOption1.isPresent()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        successorList = successorList.stream().filter(n -> {
                Optional<MNodeConnectionInfo> eInfo = serverNodeGraph.edgeValue(serverNodeId, n.getId());
                return eInfo.isPresent() && eInfo.get().getDelay() <= MServerConfig.MAX_DELAY_TOLERANCE;
            }).collect(Collectors.toList());
        return successorList;
    }

    public MNodeConnectionInfo getConnectionInfo(String fromNodeId, String toNodeId) {
        return this.serverNodeGraph.edgeValueOrDefault(fromNodeId, toNodeId, null);
    }

    /*
     * Since there are many clusters here, the ip is not unique any more.
     * We need to designate which cluster the server node belongs to
     */
    public MServerNode getByIp(String clusterId, String ip) {
        Optional<MServerCluster> clusterOptional = this.getById(clusterId);
        if (clusterOptional.isPresent()) {
            for (MServerNode node : clusterOptional.get().getNodeMap().values()) {
                if (ip.equals(node.getIp())) {
                    return node;
                }
            }
        }
        return null;
    }
}
