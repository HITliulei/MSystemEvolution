package com.septemberhx.server.utils;

import com.septemberhx.common.base.node.MServerCluster;
import com.septemberhx.common.bean.agent.MInstanceInfoBean;
import com.septemberhx.common.bean.agent.MInstanceInfoResponse;
import com.septemberhx.common.bean.server.MBuildJobBean;
import com.septemberhx.common.utils.MRequestUtils;
import com.septemberhx.common.utils.MUrlUtils;
import com.septemberhx.server.model.MServerSkeleton;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/1/1
 * <p>
 * Fetch all the variables and so on
 */
@Component
public class MServiceUtils {

    public static String BUILD_CENTER_IP;
    public static Integer BUILD_CENTER_PORT;

    @Value("${msystemevolution.orchestration.buildCenter.ip}")
    public void setBuildCenterIp(String buildCenterIp) {
        BUILD_CENTER_IP = buildCenterIp;
    }

    @Value("${msystemevolution.orchestration.buildCenter.port}")
    public void setBuildCenterPort(Integer buildCenterPort) {
        BUILD_CENTER_PORT = buildCenterPort;
    }

    public static void doBuildJob(MBuildJobBean jobBean) {
        MRequestUtils.sendRequest(
                MUrlUtils.getBuildCenterBuildUri(BUILD_CENTER_IP, BUILD_CENTER_PORT),
                jobBean,
                null,
                RequestMethod.POST
        );
    }

    public static List<MInstanceInfoBean> getInstanceInfoListByClusterId(String clusterId) {
        Optional<MServerCluster> clusterOptional = MServerSkeleton.getCurrNodeManager().getById(clusterId);
        if (clusterOptional.isPresent()) {
            MInstanceInfoResponse response = MRequestUtils.sendRequest(MUrlUtils.getMclusterFetchInstanceInfoUri(
                    clusterOptional.get().getClusterAgentIp(),
                    clusterOptional.get().getClusterAgentPort()
            ), null, MInstanceInfoResponse.class, RequestMethod.POST);

            if (response != null) {
                return response.getInfoBeanList();
            }
        }

        return new ArrayList<>();
    }
}
