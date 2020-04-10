package com.septemberhx.server.controller;

import com.septemberhx.common.base.node.MServerCluster;
import com.septemberhx.common.base.node.MServerNode;
import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.bean.agent.MInstanceInfoBean;
import com.septemberhx.common.bean.agent.MConnectionJson;
import com.septemberhx.common.bean.agent.MRegisterClusterBean;
import com.septemberhx.common.service.MSvcInstance;
import com.septemberhx.common.utils.MRequestUtils;
import com.septemberhx.common.utils.MUrlUtils;
import com.septemberhx.server.bean.MTestDeployBean;
import com.septemberhx.server.job.MDeployJob;
import com.septemberhx.server.model.MDeployExecutor;
import com.septemberhx.server.model.MDeployManager;
import com.septemberhx.server.model.MServerSkeleton;
import com.septemberhx.server.utils.MServiceUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/1/10
 *
 * The controller used for accepting system information like edge servers and so on.
 */
@RestController
@RequestMapping(value = "/cluster")
public class MClusterController {

    /**
     * Reset the node information and init the current node manager with the new one
     * @param clusterBean
     */
    @PostMapping(path = "/registerCluster")
    @ResponseBody
    public MResponse registerCluster(@RequestBody MRegisterClusterBean clusterBean) {
        // remove the old cluster if exists
        MServerSkeleton.getCurrNodeManager().remove(clusterBean.getServerCluster().getId());
        // add the cluster
        MServerSkeleton.getCurrNodeManager().add(clusterBean.getServerCluster());
        for (MConnectionJson connection : clusterBean.getConnectionList()) {
            MServerSkeleton.getCurrNodeManager().addConnectionInfo(
                    connection.getConnection(),
                    connection.getPredecessor(),
                    connection.getSuccessor()
            );
        }
        // do something if a new cluster attaches to the system, i.e., sync the instance info with the cluster
        List<MInstanceInfoBean> beanList = MServiceUtils.getInstanceInfoListByClusterId(clusterBean.getServerCluster().getId());
        for (MInstanceInfoBean infoBean : beanList) {
            this.loadInstanceInfo(infoBean);
        }
        return MResponse.successResponse();
    }

    @ResponseBody
    @PostMapping(value = "/allNodes")
    public List<MServerNode> getAllNodes() {
        return MServerSkeleton.getCurrNodeManager().allNodes();
    }

    @ResponseBody
    @PostMapping(value = "/allClusters")
    public List<MServerCluster> getAllClusters() {
        return MServerSkeleton.getCurrNodeManager().getAllValues();
    }

    @ResponseBody
    @PostMapping(value = "/allInstances")
    public List<MSvcInstance> getAllInstances() {
        return MServerSkeleton.getCurrInstManager().getAllValues();
    }

    @RequestMapping(path = "/reportInstanceInfo", method = RequestMethod.POST)
    public void loadInstanceInfo(@RequestBody MInstanceInfoBean instanceInfo) {
        MServerSkeleton.getInstance().syncInstanceInfo(instanceInfo);
    }

    @PostMapping(value = "/test/deploy")
    public void deploy(@RequestBody MTestDeployBean deployBean) {
        Optional<MServerCluster> clusterOpt = MServerSkeleton.getCurrNodeManager().getById(deployBean.getClusterId());
        if (clusterOpt.isPresent()) {
            URI uri = MUrlUtils.getMClientAgentDeployUri(
                    clusterOpt.get().getClusterAgentIp(), clusterOpt.get().getClusterAgentPort());
            MRequestUtils.sendRequest(uri, deployBean.getDeployJob().toMDeployPodRequest(), null, RequestMethod.POST);
        }
    }
}
