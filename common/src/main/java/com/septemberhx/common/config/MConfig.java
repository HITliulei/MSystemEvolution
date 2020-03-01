package com.septemberhx.common.config;

import lombok.Getter;
import lombok.Setter;

public class MConfig {

    @Getter
    @Setter
    private String mClusterHost;

    @Getter
    @Setter
    private int mClusterPort;

    @Getter
    @Setter
    private String mBuilderUrl;

    public final static String MCLUSTER_FETCH_INSTANCE_INFO = "/magent/instanceInfoList";
    public final static String MCLUSTERAGENT_REQUEST_REMOTE_URI = "/magent/remoteuri";
    public final static String MCLUSTER_SET_REST_INFO = "/mclient/setRestInfo";
    public final static String MCLIENT_SET_APICS_URI = "/mclient/setApiContinueStatus";
    public final static String MCLUSTERAGENT_SET_REST_INFO = "/magent/setRestInfo";
    public final static String MCLUSTERAGENT_DEPLOY_URI = "/magent/deploy";
    public final static String MCLUSTERAGENT_SET_APICS_URI = "/magent/setApiContinueStatus";
    public final static String MCLUSTERAGENT_FETCH_LOGS = "/magent/fetchLogsBetweenTime";
    public static final String MCLUSTERAGENT_DELETE_URI = "/magent/deleteInstance";
    public final static String MCLUSTERAGNET_FETCH_REQUEST_URL = "/magent/fetchRequestUrl";
    public final static String MCLUSTERAGENT_ALL_USER_URL = "/magent/allUser";
    public final static String MCLUSTERAGENT_UPDATE_GATEWAYS = "/magent/updateGateways";
    public final static String MCLUSTERAGENT_DO_REQUEST_URL = "/magent/doRequest";
    public final static String MCLUSTERAGENT_INSTANCE_REGISTER_URL = "/magent/registered";

    public final static String MCLUSTERAGENT_FETCH_CLIENT_INFO = "/mclient/info";
    public final static String MSERVER_GET_REMOTE_URI = "/mserver/remoteuri";
    public final static String MSERVER_JOB_NOTIFY_URI = "/mserver/notifyJob";
    public final static String MSERVER_DEPLOY_JOB_NOTIFY_URI = "/mserver/notifyDeployJob";
    public final static String MSERVER_FETCH_REQUEST_URL = "/mserver/fetchRequestUrl";
    public final static String MSERVER_DO_REQUEST_URL = "/mserver/doRequest";

    public final static String MCLUSTER_DOCKER_NAMESPACE = "kube-test";

    public final static String BUILD_CENTER_BUILD_URI = "/buildcenter/build";
    public final static String BUILD_CENTER_CBUILD_URI = "/buildcenter/cbuild";

    public final static String MGATEWAY_UPDATE_URI = "/update";
    public final static String MGATEWAY_ALL_USER_URI = "/allUser";


    public final static String K8S_NAMESPACE = "kube-test";
    public final static String MCLUSTERAGENT_NAME = "MClusterAgent";

    public final static String REQUEST_SHOULD_SEND_TO_CLOUD = "request should send to cloud";

    // ------
    public final static String SERVICE_NAME_SERVER = "MOrchestrationServer";
    public final static String SERVER_SERVICE_INFO_CALLBACK_URI = "/service/pushServiceInfos";
    // ------
    public final static String SERVICE_NAME_ANALYZE = "MServiceAnalyser";
    public final static String ANALYZE_ANALYZE_URI = "/analyzer";
    public final static String ANALYZE_COMPARE_URI = "/compare";

    // --- new version ---
    public final static String MSERVER_CLUSTER_REPORT_INSTANCEINFO = "/cluster/reportInstanceInfo";
    public final static String MSERVER_CLUSTER_REGISTER = "/cluster/registerCluster";

    public final static String MCLUSTER_SVC_METADATA_NAME = "mclient";
    public final static String MCLUSTER_SVC_METADATA_VALUE = "true";
    public final static String MCLUSTER_SVC_VER_NAME = "serviceVersion";

    public final static String MGATEWAY_NAME = "MGateway";
    public final static String MGATEWAY_DEPENDENCY_CALL = "/call/dependency";
    public final static String MGATEWAY_DEPENDENCY_ID = "__dependency_id";

    private static MConfig ourInstance = new MConfig();

    public static MConfig getInstance() {
        return ourInstance;
    }

    private MConfig() {
        this.setMClusterHost("192.168.1.102");
        this.setMClusterPort(9000);
    }
}
