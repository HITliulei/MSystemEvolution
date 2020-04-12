package com.septemberhx.server.adaptation;

import com.septemberhx.common.base.node.MServerCluster;
import com.septemberhx.common.bean.MTimeIntervalBean;
import com.septemberhx.common.bean.gateway.MDepRequestCountBean;
import com.septemberhx.common.bean.gateway.MDepRequestCountListBean;
import com.septemberhx.common.config.MConfig;
import com.septemberhx.common.service.dependency.PureSvcDependency;
import com.septemberhx.common.utils.MRequestUtils;
import com.septemberhx.common.utils.MUrlUtils;
import com.septemberhx.server.model.MDeployExecutor;
import com.septemberhx.server.model.MDeployManager;
import com.septemberhx.server.model.MServerSkeleton;
import com.septemberhx.server.algorithm.dep.MDepAlgorithm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMethod;

import java.net.URI;
import java.util.HashMap;
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
            MDepRequestCountListBean depListBean = this.fetchDepList(cluster, intervalBean);

            // 2. analyze
            Map<String, Map<PureSvcDependency, Integer>> demandCountMap = new HashMap<>();
            for (MDepRequestCountBean bean : depListBean.getCountList()) {
                demandCountMap.put(bean.getNodeId(), bean.depUserCountMap());
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
            MDeployExecutor executor = new MDeployExecutor(
                    deployTopology, MServerSkeleton.getInstance().getCurrSystemModel(), cluster.getId());
            MServerSkeleton.getInstance().setExecutor(executor);
            executor.execute();
        }
    }

    private MDepRequestCountListBean fetchDepList(MServerCluster cluster, MTimeIntervalBean intervalBean) {
        URI uri = MUrlUtils.getRemoteUri(
                cluster.getClusterAgentIp(), cluster.getClusterAgentPort(), MConfig.MCLUSTER_FETCH_DEP_REQUEST_COUNT);
        return MRequestUtils.sendRequest(uri, intervalBean, MDepRequestCountListBean.class, RequestMethod.POST);
    }
}
