package com.septemberhx.server.controller;

import com.septemberhx.server.bean.MConnectionJson;
import com.septemberhx.server.bean.MRegisterClusterBean;
import com.septemberhx.server.model.MServerSkeleton;
import org.springframework.web.bind.annotation.*;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/1/10
 *
 * The controller used for accepting system information like edge servers and so on.
 */
@RestController
public class MClusterController {

    /**
     * Reset the node information and init the current node manager with the new one
     * @param nodesBean
     */
    @PostMapping(path = "/registerCluster")
    public void registerCluster(@RequestBody MRegisterClusterBean clusterBean) {
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
        // todo: do something if a new cluster attaches to the system
    }
}
