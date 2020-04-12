package com.septemberhx.server.adaptation;

import com.septemberhx.common.base.node.MServerCluster;
import com.septemberhx.common.bean.MTimeIntervalBean;
import com.septemberhx.common.bean.gateway.MDepRequestCacheBean;
import com.septemberhx.common.bean.gateway.MDepRequestCacheListBean;
import com.septemberhx.common.config.MConfig;
import com.septemberhx.common.service.dependency.PureSvcDependency;
import com.septemberhx.common.utils.MRequestUtils;
import com.septemberhx.common.utils.MUrlUtils;
import com.septemberhx.server.model.MDeployManager;
import com.septemberhx.server.model.MServerSkeleton;
import com.septemberhx.server.algorithm.dep.MDepAlgorithm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMethod;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/4/12
 */
public class MDepAdaptation implements MAdaptationInterface {

    private static Logger logger = LogManager.getLogger(MDepAdaptation.class);

    @Override
    public void evolve(MTimeIntervalBean intervalBean) {

        for (MServerCluster cluster : MServerSkeleton.getCurrNodeManager().getAllValues()) {
            // 1. monitor
            MDepRequestCacheListBean depListBean = this.fetchDepList(cluster, intervalBean);

            // 2. analyze
            Map<String, Map<PureSvcDependency, Integer>> demandCountMap = new HashMap<>();
            Map<String, Map<PureSvcDependency, Set<String>>> demandCountSet = new HashMap<>();
            for (MDepRequestCacheBean bean : depListBean.getRequestList()) {
                if (!demandCountSet.containsKey(bean.getNodeId())) {
                    demandCountSet.put(bean.getNodeId(), new HashMap<>());
                }

                if (!demandCountSet.get(bean.getNodeId()).containsKey(bean.getBaseSvcDependency().getDep())) {
                    demandCountSet.get(bean.getNodeId()).put(bean.getBaseSvcDependency().getDep(), new HashSet<>());
                }
                demandCountSet.get(bean.getNodeId()).get(bean.getBaseSvcDependency().getDep()).add(bean.getClientId());
            }
            for (String nodeId : demandCountSet.keySet()) {
                if (!demandCountMap.containsKey(nodeId)) {
                    demandCountMap.put(nodeId, new HashMap<>());
                }
                for (PureSvcDependency dep : demandCountSet.get(nodeId).keySet()) {
                    demandCountMap.get(nodeId).put(dep, demandCountSet.get(nodeId).get(dep).size());
                }
            }

            // 3. plan
            MDeployManager deployTopology = MDepAlgorithm.getSuggestedTopology(
                    demandCountMap, demandCountMap,
                    MServerSkeleton.getCurrSvcManager(),
                    MServerSkeleton.getCurrNodeManager(),
                    cluster.getId()
            );

            // 4. execute
            logger.info(deployTopology);
        }
    }

    private MDepRequestCacheListBean fetchDepList(MServerCluster cluster, MTimeIntervalBean intervalBean) {
        URI uri = MUrlUtils.getRemoteUri(
                cluster.getClusterAgentIp(), cluster.getClusterAgentPort(), MConfig.MCLUSTER_FETCH_DEP_REQUEST);
        return MRequestUtils.sendRequest(uri, intervalBean, MDepRequestCacheListBean.class, RequestMethod.POST);
    }
}
