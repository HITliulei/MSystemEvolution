package com.septemberhx.common.utils;

import com.septemberhx.common.bean.MRoutingBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import com.septemberhx.common.config.MConfig;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MUrlUtils {

    private final static Logger logger = LogManager.getLogger(MUrlUtils.class);

    /**
     * Construct the uri to fetch all the instance info in the cluster
     * @return URI
     */
    public static URI getMclusterFetchInstanceInfoUri(String ipAddr, int port) {
            return MUrlUtils.getRemoteUri(
                    ipAddr,
                    port,
                    MConfig.MCLUSTER_FETCH_INSTANCE_INFO
            );
    }

    /**
     * Construct the uri to fetch the parentIdMap for given INSTANCE IP ADDRESS
     * @param instanceIpAddr: ip address of given instance
     * @param port: service port
     * @return URI
     */
    public static URI getMClusterAgentFetchClientInfoUri(String instanceIpAddr, int port) {
        return MUrlUtils.getRemoteUri(instanceIpAddr, port, MConfig.MCLUSTERAGENT_FETCH_CLIENT_INFO);
    }

    public static URI getMClientRequestRemoteUri(String clusterAgentIpAddr, int port) {
        return MUrlUtils.getRemoteUri(clusterAgentIpAddr, port, MConfig.MCLUSTERAGENT_REQUEST_REMOTE_URI);
    }

    public static URI getMClientAgentSetRestInfoUri(String clusterAgentIpAddr, int port) {
        return MUrlUtils.getRemoteUri(clusterAgentIpAddr, port, MConfig.MCLUSTERAGENT_SET_REST_INFO);
    }

    public static URI getMServerRemoteUri(String serverIpAddr, int serverPort) {
        return MUrlUtils.getRemoteUri(serverIpAddr, serverPort, MConfig.MSERVER_GET_REMOTE_URI);
    }

    public static URI getMServerLoadInstanceInfoUri(String serverIpAddr, int serverPort) {
        return MUrlUtils.getRemoteUri(serverIpAddr, serverPort, MConfig.MSERVER_CLUSTER_REPORT_INSTANCEINFO);
    }

    public static URI getBuildCenterBuildUri(String buildCenterIpAddr, int buildCenterPort) {
        return MUrlUtils.getRemoteUri(buildCenterIpAddr, buildCenterPort, MConfig.BUILD_CENTER_BUILD_URI);
    }

    public static URI getBuildCenterCBuildUri(String buildCenterIpAddr, int buildCenterPort) {
        return MUrlUtils.getRemoteUri(buildCenterIpAddr, buildCenterPort, MConfig.BUILD_CENTER_CBUILD_URI);
    }

    public static URI getMClientAgentDeployUri(String clusterAgentIpAddr, int port) {
        return MUrlUtils.getRemoteUri(clusterAgentIpAddr, port, MConfig.MCLUSTERAGENT_DEPLOY_URI);
    }

    public static URI getMServerNotifyJobUri(String serverIpAddr, int serverPort) {
        return MUrlUtils.getRemoteUri(serverIpAddr, serverPort, MConfig.MSERVER_JOB_NOTIFY_URI);
    }

    public static URI getMServerDeployNotifyJobUri(String serverIpAddr, int serverPort) {
        return MUrlUtils.getRemoteUri(serverIpAddr, serverPort, MConfig.MSERVER_DEPLOY_JOB_NOTIFY_URI);
    }

    public static URI getMClientSetApiCStatus(String instanceIpAddr, int port) {
        return MUrlUtils.getRemoteUri(instanceIpAddr, port, MConfig.MCLIENT_SET_APICS_URI);
    }

    public static URI getMClusterAgentSetApiCStatus(String clusterAgentIpAddr, int port) {
        return MUrlUtils.getRemoteUri(clusterAgentIpAddr, port, MConfig.MCLUSTERAGENT_SET_APICS_URI);
    }

    public static URI getMClusterAgentFetchLogsByTime(String clusterAgentIpAddr, int port) {
        return MUrlUtils.getRemoteUri(clusterAgentIpAddr, port, MConfig.MCLUSTERAGENT_FETCH_LOGS);
    }

    public static URI getMClusterAgentDeleteInstanceUri(String clusterAgentIpAddr, int port) {
        return MUrlUtils.getRemoteUri(clusterAgentIpAddr, port, MConfig.MCLUSTERAGENT_DELETE_URI);
    }

    public static URI getMServerFetchRequestUrl(String clusterAgentIpAddr, int port) {
        return MUrlUtils.getRemoteUri(clusterAgentIpAddr, port, MConfig.MSERVER_FETCH_REQUEST_URL);
    }

    public static URI getMClusterFetchRequestUrl(String clusterIpAddr, int port) {
        return MUrlUtils.getRemoteUri(clusterIpAddr, port, MConfig.MCLUSTERAGNET_FETCH_REQUEST_URL);
    }

    public static URI getMGatewayUpdateUri(String ipAddr, int port) {
        return MUrlUtils.getRemoteUri(ipAddr, port, MConfig.MGATEWAY_UPDATE_URI);
    }

    public static URI getMGatewayFetchRequestsUri(String ipAddr, int port) {
        return MUrlUtils.getRemoteUri(ipAddr, port, MConfig.MGATEWAY_FETCH_REQUESTS);
    }

    public static URI getMGatewayAllUserUri(String ipAddr, int port) {
        return MUrlUtils.getRemoteUri(ipAddr, port, MConfig.MGATEWAY_ALL_USER_URI);
    }

    public static URI getMClusterAllUserUrl(String ipAddr, int port) {
        return MUrlUtils.getRemoteUri(ipAddr, port, MConfig.MCLUSTERAGENT_ALL_USER_URL);
    }

    public static URI getMClusterUpdateGateways(String ipAddr, int port) {
        return MUrlUtils.getRemoteUri(ipAddr, port, MConfig.MCLUSTERAGENT_UPDATE_GATEWAYS);
    }

    public static URI getMServerDoRequestUri(String ipAddr, int port) {
        return MUrlUtils.getRemoteUri(ipAddr, port, MConfig.MSERVER_DO_REQUEST_URL);
    }

    public static URI getMClusterDoRequestUri(String ipAddr, int port) {
        return MUrlUtils.getRemoteUri(ipAddr, port, MConfig.MCLUSTERAGENT_DO_REQUEST_URL);
    }

    public static URI getRemoteUri(MRoutingBean routingBean) {
        return MUrlUtils.getRemoteUri(
                routingBean.getIpAddr(), routingBean.getPort(), routingBean.getPatternUrl()
        );
    }

    public static URI getRemoteUri(String ipAddr, int port, String path) {
        URI uri = null;
        try {
            uri = new URI(
                    "http",
                    null,
                    ipAddr,
                    port,
                    path, null, null
            );
        } catch (URISyntaxException e) {
            logger.debug(e);
        }
        logger.debug(uri);
        return uri;
    }

    public static URI getRemoteUriWithQueries(URI oldUri, Map<String, String> paramMap) {
        URI uri = null;
        LinkedMultiValueMap<String, String> pMap = new LinkedMultiValueMap<>();
        for (String key : paramMap.keySet()) {
            List<String> vList = new ArrayList<>(1);
            vList.add(paramMap.get(key));
            pMap.put(key, vList);
        }
        try {
            uri = UriComponentsBuilder.fromUri(oldUri).queryParams(pMap).build().toUri();
        } catch (Exception e) {
            logger.debug(e);
        }
        return uri;
    }

    public static URI getMClusterSetRestInfoUri(String instanceIpAddr, int port) {
        return MUrlUtils.getRemoteUri(instanceIpAddr, port, MConfig.MCLUSTER_SET_REST_INFO);
    }
}
