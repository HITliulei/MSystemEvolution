package com.septemberhx.server.model;

import com.septemberhx.common.base.node.MServerCluster;
import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.bean.agent.MDepResetRoutingBean;
import com.septemberhx.common.bean.server.MUpdateCopyInstBean;
import com.septemberhx.common.config.MConfig;
import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.MSvcInstance;
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
public class MDeployExecutor {

    enum NodeJobState {
        COPY,  // copy removed instances to cloud
        DEPLOY,  // deploy new instance or delete new instance
        CLEAN  // delete the instance copy on cloud
    }

    private List<String> nodeIdList = new ArrayList<>();
    private String currNodeId;
    private String clusterId;
    private NodeJobState nodeJobState;

    private MSystemModel currModel;
    private Map<String, Map<String, Integer>> diffMap = new HashMap<>();

    private Map<String, String> copyInstsMap = new HashMap<>();
    private Set<String> doingJobIdSet = new HashSet<>();
    private List<MSvcInstance> removedInstList = new ArrayList<>();

    private Random random = new Random(1000000);
    private static Logger logger = LogManager.getLogger(MDeployExecutor.class);

    public MDeployExecutor(MDeployManager deployManager, MSystemModel currModel, String clusterId) {
        this.currModel = currModel;
        this.diffMap = MDeployAlgos.diff(deployManager, currModel);
        this.nodeIdList = new ArrayList<>(this.diffMap.keySet());
        this.clusterId = clusterId;
    }

    public void execute() {
        if (this.checkIfFinished()) {
            MDepResetRoutingBean resetRoutingBean = new MDepResetRoutingBean();

            return;
        }

        if (this.currNodeId == null && this.nodeIdList.size() > 0) {
            this.currNodeId = nodeIdList.get(0);
            nodeIdList.remove(0);

            this.nodeJobState = NodeJobState.COPY;
            this.removedInstList.clear();
            if (this.diffMap.containsKey(this.currNodeId)) {
                for (String svcId : diffMap.get(this.currNodeId).keySet()) {
                    if (diffMap.get(this.currNodeId).get(svcId) >= 0) {
                        continue;
                    }

                    List<MSvcInstance> svcInstanceList =
                            this.currModel.getInstanceManager().getInstanceByNodeId(this.currNodeId);
                    Collections.shuffle(svcInstanceList, this.random);
                    for (int i = 0; i < Math.abs(diffMap.get(this.currNodeId).get(svcId)); ++i) {
                        this.removedInstList.add(svcInstanceList.get(i));
                    }
                }
            }

            if (this.removedInstList.isEmpty()) {
                this.execute();
                return;
            }

            for (MSvcInstance inst : this.removedInstList) {
                String newInstId = MIDUtils.uniqueInstanceId(inst.getServiceName(), inst.getVersion());
                String cloudNodeId = this.getOneCloudId();
                String jobId = this.deployInstanceOnCloud(cloudNodeId, inst.getServiceId(), newInstId);
                this.copyInstsMap.put(inst.getIp(), newInstId);
                this.doingJobIdSet.add(jobId);
            }
        } else if (this.nodeJobState == NodeJobState.COPY) {
            if (!this.doingJobIdSet.isEmpty()) {
                return;
            }

            this.nodeJobState = NodeJobState.DEPLOY;
            if (this.notifyCopyInsts()) {
                Map<String, Integer> svcAddMap = new HashMap<>();
                if (this.diffMap.containsKey(this.currNodeId)) {
                    for (String svcId : diffMap.get(this.currNodeId).keySet()) {
                        if (diffMap.get(this.currNodeId).get(svcId) <= 0) {
                            continue;
                        }
                        svcAddMap.put(svcId, diffMap.get(this.currNodeId).get(svcId));
                    }
                }

                if (svcAddMap.isEmpty()) {
                    this.execute();
                    return;
                }

                for (String svcId : svcAddMap.keySet()) {
                    Optional<MService> svcOpt = this.currModel.getServiceManager().getById(svcId);
                    if (svcOpt.isPresent()) {
                        String newInstId = MIDUtils.uniqueInstanceId(svcOpt.get().getServiceName(), svcOpt.get().getServiceVersion().toString());
                        String jobId = this.deployInstanceOnCluster(this.clusterId, this.currNodeId, svcId, newInstId);
                        this.doingJobIdSet.add(jobId);
                    }
                }

                for (MSvcInstance inst : this.removedInstList) {
                    String jobId = this.deleteInstanceOnCluster(this.clusterId, inst.getNodeId(), inst.getServiceId(), inst.getPodId());
                    this.doingJobIdSet.add(jobId);
                }

                if (this.doingJobIdSet.isEmpty()) {
                    this.execute();
                }
            }
        } else if (this.nodeJobState == NodeJobState.DEPLOY) {
            if (!this.doingJobIdSet.isEmpty()) {
                return;
            }

            this.nodeJobState = NodeJobState.CLEAN;
            for (String instId : this.copyInstsMap.values()) {
                String jobId = this.deleteInstanceOnCloud(instId);
                this.doingJobIdSet.add(jobId);
            }

            if (this.doingJobIdSet.isEmpty()) {
                this.execute();
            }
        } else if (this.nodeJobState == NodeJobState.CLEAN) {
            if (!this.doingJobIdSet.isEmpty()) {
                return;
            }

            this.nodeJobState = null;
            this.currNodeId = null;
            this.execute();
        }
    }

    public boolean checkIfFinished() {
        return this.nodeIdList.isEmpty() && this.currNodeId == null;
    }

    public boolean notifyCopyInsts() {
        Pair<String, Integer> agentInfo = this.getClusterAgentInfo(clusterId);
        URI uri = MUrlUtils.getRemoteUri(agentInfo.getValue0(), agentInfo.getValue1(), MConfig.MCLUSTER_UPDATE_COPY_MAP);
        MResponse response = MRequestUtils.sendRequest(
                uri, new MUpdateCopyInstBean(this.copyInstsMap), MResponse.class, RequestMethod.POST
        );
        return true;
    }

    public String getOneCloudId() {
        return this.currModel.getNodeManager().getCloudNodes().get(0).getId();
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
