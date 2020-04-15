package com.septemberhx.server.model;

import com.septemberhx.common.base.node.MServerCluster;
import com.septemberhx.common.base.node.MServerNode;
import com.septemberhx.common.base.node.ServerNodeType;
import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.bean.agent.MDepResetRoutingBean;
import com.septemberhx.common.bean.server.MUpdateCopyInstBean;
import com.septemberhx.common.config.MConfig;
import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.MSvcInstance;
import com.septemberhx.common.service.MSvcInterface;
import com.septemberhx.common.utils.MRequestUtils;
import com.septemberhx.common.utils.MUrlUtils;
import com.septemberhx.server.algorithm.dep.MDeployAlgos;
import com.septemberhx.server.job.MDeleteJob;
import com.septemberhx.server.job.MDeployJob;
import com.septemberhx.server.utils.MIDUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javatuples.Pair;
import org.springframework.web.bind.annotation.RequestMethod;

import java.net.URI;
import java.util.*;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/21
 */
public class MDeployExecutorSimple implements MDeployExecutorInterface {

    enum NodeJobState {
        IDLE,
        COPY,  // copy removed instances to cloud
        DEPLOY,  // deploy new instance or delete new instance
        CLEAN  // delete the instance copy on cloud
    }

    private List<String> nodeIdList = new ArrayList<>();
    private String clusterId;
    private NodeJobState nodeJobState;

    private MSystemModel currModel;
    private Map<String, Map<String, Integer>> diffMap = new HashMap<>();

    private Set<String> doingJobIdSet = new HashSet<>();

    private Random random = new Random(1000000);
    private MDeployManager deployManager;
    private static Logger logger = LogManager.getLogger(MDeployExecutorSimple.class);
    private Map<String, List<MSvcInstance>> deletedInstMap = new HashMap<>();
    private Set<String> deletedInstIdSet = new HashSet<>();

    public MDeployExecutorSimple(MDeployManager deployManager, MSystemModel currModel, String clusterId) {
        this.currModel = currModel;
        this.diffMap = MDeployAlgos.diff(deployManager, currModel);
        this.nodeIdList = new ArrayList<>(this.diffMap.keySet());
        this.clusterId = clusterId;
        this.deployManager = deployManager;
        this.nodeJobState = NodeJobState.IDLE;
    }

    public void execute() {
        if (this.nodeJobState == NodeJobState.IDLE) {

            this.nodeJobState = NodeJobState.DEPLOY;
            this.deletedInstMap.clear();
            this.deletedInstIdSet.clear();

            for (String currNodeId : this.nodeIdList) {
                this.deletedInstMap.put(currNodeId, new ArrayList<>());
                Map<String, Integer> svcAddMap = new HashMap<>();
                if (this.diffMap.containsKey(currNodeId)) {
                    for (String svcId : diffMap.get(currNodeId).keySet()) {
                        if (diffMap.get(currNodeId).get(svcId) > 0) {
                            svcAddMap.put(svcId, diffMap.get(currNodeId).get(svcId));
                        } else if (diffMap.get(currNodeId).get(svcId) < 0) {
                            List<MSvcInstance> svcInstanceList =
                                    this.currModel.getInstanceManager().getInstByNodeIdAndSvcId(currNodeId, svcId);
                            Collections.shuffle(svcInstanceList, this.random);
                            for (int i = 0; i < Math.abs(diffMap.get(currNodeId).get(svcId)); ++i) {
                                this.deletedInstMap.get(currNodeId).add(svcInstanceList.get(i));
                                this.deletedInstIdSet.add(svcInstanceList.get(i).getId());
                            }
                        }
                    }
                }

                for (String svcId : svcAddMap.keySet()) {
                    Optional<MService> svcOpt = this.currModel.getServiceManager().getById(svcId);
                    if (svcOpt.isPresent()) {
                        String newInstId = MIDUtils.uniqueInstanceId(svcOpt.get().getServiceName(), svcOpt.get().getServiceVersion().toString());
                        Optional<MServerNode> nodeOpt = this.currModel.getNodeManager().getNodeById(currNodeId);
                        if (nodeOpt.isPresent()) {
                            String jobId = null;
                            if (nodeOpt.get().getNodeType() == ServerNodeType.EDGE) {
                                jobId = this.deployInstanceOnCluster(this.clusterId, currNodeId, svcId, newInstId);
                            } else if (nodeOpt.get().getNodeType() == ServerNodeType.CLOUD) {
                                jobId = this.deployInstanceOnCloud(this.currModel.getNodeManager().getCloudNodes().get(0).getId(), svcId, newInstId);
                            }
                            this.doingJobIdSet.add(jobId);
                        }
                    }
                }
                this.execute();
            }
        } else if (this.nodeJobState == NodeJobState.DEPLOY) {
            if (!this.doingJobIdSet.isEmpty()) {
                return;
            }

            {
                MDepResetRoutingBean resetRoutingBean = new MDepResetRoutingBean();
                Map<String, MSvcInstance> instMap = new HashMap<>();
                for (MSvcInstance instance : this.currModel.getInstanceManager().getAllValues()) {
                    if (!this.deletedInstIdSet.contains(instance.getId())) {
                        instMap.put(instance.getIp(), instance);
                    }
                }
                resetRoutingBean.setInstMap(instMap);

                Map<String, MService> svcMap = new HashMap<>();
                for (MService svc : this.currModel.getServiceManager().getAllValues()) {
                    svcMap.put(svc.getId(), svc);
                }
                resetRoutingBean.setServiceMap(svcMap);
                resetRoutingBean.putInstValues(this.deployManager.getNodeDepSvcMap());
                resetRoutingBean.putUserValues(this.deployManager.getNodeUserDepSvcMap());
                resetRoutingBean.setNodeDelayMap(this.currModel.getNodeManager().getNodeDelayMap(this.clusterId));

                Pair<String, Integer> agentInfo = this.getClusterAgentInfo(this.clusterId);
                URI uri = MUrlUtils.getRemoteUri(agentInfo.getValue0(), agentInfo.getValue1(), MConfig.MCLUSTER_DEP_ROUTING_RESET);
                MRequestUtils.sendRequest(uri, resetRoutingBean, null, RequestMethod.POST);
            }

            this.nodeJobState = NodeJobState.CLEAN;
            for (String currNodeId : this.deletedInstMap.keySet()) {
                Optional<MServerNode> nodeOpt = this.currModel.getNodeManager().getNodeById(currNodeId);
                for (MSvcInstance svcInstance : this.deletedInstMap.get(currNodeId)) {
                    if (nodeOpt.isPresent()) {
                        String jobId = null;
                        if (nodeOpt.get().getNodeType() == ServerNodeType.EDGE) {
                            jobId = this.deleteInstanceOnCluster(this.clusterId, svcInstance.getNodeId(),
                                        svcInstance.getServiceId(), svcInstance.getPodId());
                        } else if (nodeOpt.get().getNodeType() == ServerNodeType.CLOUD) {
                            jobId = this.deleteInstanceOnCloud(svcInstance.getPodId());
                        }
                        this.doingJobIdSet.add(jobId);
                    }
                }
            }
            this.execute();
        } else if (this.nodeJobState == NodeJobState.CLEAN) {
            if (!this.doingJobIdSet.isEmpty()) {
                return;
            }

            this.nodeJobState = NodeJobState.IDLE;
        }
    }

    public boolean checkIfFinished() {
        return this.doingJobIdSet.isEmpty() && this.nodeJobState == NodeJobState.IDLE;
    }

    /*
     * Create a service instance of service `serviceId` with pod id `podId` on node `cloudNodeId`
     * return job id
     */
    public String deployInstanceOnCloud(String cloudNodeId, String serviceId, String podId) {
        Optional<MService> svcOpt = this.currModel.getServiceManager().getById(serviceId);
        if (svcOpt.isPresent()) {
            MDeployJob deployJob = new MDeployJob(cloudNodeId, svcOpt.get().getServiceName(), podId, svcOpt.get().getImageUrl());
            Pair<String, Integer> agentInfo = this.getCloudAgentInfo();
            if (agentInfo != null) {
                URI uri = MUrlUtils.getMClientAgentDeployUri(agentInfo.getValue0(), agentInfo.getValue1());
                if (uri != null) {
                    MRequestUtils.sendRequest(uri, deployJob.toMDeployPodRequest(), null, RequestMethod.POST);
                }
            }
            return deployJob.getId();
        } else {
            logger.error(String.format(
                    "The system tries to deploy a instance of %s to node %s, but the service %s doesn't exist",
                    serviceId, cloudNodeId, serviceId
            ));
            return null;
        }
    }

    /*
     * Create a service instance of service `serviceId` with pod id `podId` on node `cloudNodeId`
     * return job id
     */
    public String deployInstanceOnCluster(String clusterId, String clusterNodeId, String serviceId, String podId) {
        Optional<MService> svcOpt = this.currModel.getServiceManager().getById(serviceId);
        if (svcOpt.isPresent()) {
            MDeployJob deployJob = new MDeployJob(clusterNodeId, svcOpt.get().getServiceName(), podId, svcOpt.get().getImageUrl());
            Pair<String, Integer> agentInfo = this.getClusterAgentInfo(clusterId);
            if (agentInfo != null) {
                URI uri = MUrlUtils.getMClientAgentDeployUri(agentInfo.getValue0(), agentInfo.getValue1());
                if (uri != null) {
                    MRequestUtils.sendRequest(uri, deployJob.toMDeployPodRequest(), null, RequestMethod.POST);
                }
            }
            return deployJob.getId();
        } else {
            logger.error(String.format(
                    "The system tries to deploy a instance of %s to node %s, but the service %s doesn't exist",
                    serviceId, clusterNodeId, serviceId
            ));
            return null;
        }
    }

    public String deleteInstanceOnCluster(String clusterId, String nodeId, String svcId, String podId) {
        MDeleteJob deleteJob = new MDeleteJob(podId, svcId, nodeId);
        Map<String, String> paras = new HashMap<>();
        paras.put("dockerInstanceId", deleteJob.getInstanceId());
        Pair<String, Integer> agentInfo = this.getClusterAgentInfo(clusterId);
        if (agentInfo != null) {
            URI uri = MUrlUtils.getMClusterAgentDeleteInstanceUri(agentInfo.getValue0(), agentInfo.getValue1());
            if (uri != null) {
                MRequestUtils.sendRequest(uri, paras, null, RequestMethod.GET);
            }
        }
        return deleteJob.getId();
    }

    public String deleteInstanceOnCloud(String podId) {
        MDeleteJob deleteJob = new MDeleteJob(podId, null, null);
        Map<String, String> paras = new HashMap<>();
        paras.put("dockerInstanceId", deleteJob.getInstanceId());
        Pair<String, Integer> agentInfo = this.getCloudAgentInfo();
        if (agentInfo != null) {
            URI uri = MUrlUtils.getMClusterAgentDeleteInstanceUri(agentInfo.getValue0(), agentInfo.getValue1());
            if (uri != null) {
                MRequestUtils.sendRequest(uri, paras, null, RequestMethod.GET);
            }
        }
        return deleteJob.getId();
    }

    public void jobFinished(String jobId) {
        this.doingJobIdSet.remove(jobId);
        this.execute();
    }

    public Pair<String, Integer> getCloudAgentInfo() {
        Optional<MServerCluster> clusterOpt = this.currModel.getNodeManager().getCloudCluster();
        return clusterOpt.map(mServerCluster -> new Pair<>(mServerCluster.getClusterAgentIp(), mServerCluster.getClusterAgentPort())).orElse(null);
    }

    public Pair<String, Integer> getClusterAgentInfo(String clusterId) {
        Optional<MServerCluster> clusterOpt = this.currModel.getNodeManager().getById(clusterId);
        return clusterOpt.map(mServerCluster -> new Pair<>(mServerCluster.getClusterAgentIp(), mServerCluster.getClusterAgentPort())).orElse(null);
    }
}
