package com.septemberhx.agent.utils;

import com.netflix.appinfo.InstanceInfo;
import com.septemberhx.agent.middleware.MDockerManager;
import com.septemberhx.agent.middleware.MDockerManagerK8SImpl;
import com.septemberhx.agent.middleware.MServiceManager;
import com.septemberhx.common.bean.agent.MDeployNotifyRequest;
import com.septemberhx.common.bean.agent.MDeployPodRequest;
import com.septemberhx.common.bean.agent.MInstanceInfoBean;
import com.septemberhx.common.bean.mclient.MClientInfoBean;
import com.septemberhx.common.bean.mclient.MInstanceRestInfoBean;
import com.septemberhx.common.config.MConfig;
import com.septemberhx.common.service.MSvcVersion;
import com.septemberhx.common.utils.MRequestUtils;
import com.septemberhx.common.utils.MUrlUtils;
import io.kubernetes.client.models.*;
import io.kubernetes.client.util.Yaml;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class MClientUtils {

    @Autowired
    private MServiceManager clusterMiddleware;

    @Value("${mvf4ms.center.ip}")
    private String serverIpAddr;

    @Value("${mvf4ms.center.port}")

    private Integer serverPort;
    private static MDockerManager dockerManager = new MDockerManagerK8SImpl();
    private Map<String, MDeployPodRequest> podDuringDeploying = new HashMap<>();  // deployed but not running
    private static Logger logger = LogManager.getLogger(MClientUtils.class);

    public static void sendRestInfo(URI uri, MInstanceRestInfoBean infoBean) {
        MRequestUtils.sendRequest(uri, infoBean, Object.class, RequestMethod.POST);
    }

    public static Boolean deleteInstanceById(String instanceId) {
        return dockerManager.deleteInstanceById(instanceId);
    }

    public static V1Deployment buildDeployment(String serviceName, String serviceInstanceId, String nodeId, String image) {
        String deploymentName = serviceName + "-" + serviceInstanceId;

        // read pod configure file supplied by users
        V1Pod pod = readPodYaml(serviceName);
        if (pod == null) {
            throw new RuntimeException("Cannot get the yaml file for service: " + serviceName);
        }

        // fill the container image
        pod.getMetadata().getLabels().put("app", deploymentName);
        for (V1Container container : pod.getSpec().getContainers()) {
            if (container.getName().equals(serviceName)) {
                container.setImage(image);
            }
        }
        // fill the node selector
        if (pod.getSpec().getNodeSelector() == null) {
            pod.getSpec().setNodeSelector(new HashMap<>());
        }
        pod.getSpec().getNodeSelector().put("node", nodeId);

        // build deployment configure file
        V1PodTemplateSpec podTemplateSpec = new V1PodTemplateSpec();
        podTemplateSpec.setSpec(pod.getSpec());
        podTemplateSpec.setMetadata(pod.getMetadata());
        V1Deployment deployment =
                new V1DeploymentBuilder(true)
                    .withApiVersion("extensions/v1beta1")
                    .withKind("Deployment")
                    .withNewMetadata()
                    .withNamespace(MConfig.MCLUSTER_DOCKER_NAMESPACE)
                    .withName(deploymentName)
                    .endMetadata()
                    .withNewSpec()
                    .withReplicas(1)
                    .withTemplate(podTemplateSpec)
                    .endSpec()
                    .build();
        // save the configure file
        try {
            FileWriter writer = new FileWriter("./test.yaml");
            Yaml.dump(deployment, writer);
        } catch (Exception e) {
            logger.debug(e);
        }
        return deployment;
    }

    public static V1Pod readPodYaml(String serviceName) {
        V1Pod pod = null;
        try {
            Object podYamlObj = Yaml.load(new File("yaml" + File.separator +serviceName + ".yaml"));
            if (podYamlObj instanceof V1Pod) {
                pod = (V1Pod) podYamlObj;
            }
        } catch (Exception e) {
            logger.debug(e);
        }
        return pod;
    }

    public List<MInstanceInfoBean> getInstanceInfoList() {
        List<MInstanceInfoBean> result = new ArrayList<>();
        for (InstanceInfo info : this.clusterMiddleware.getInstanceInfoList()) {
            MInstanceInfoBean infoBean = this.transformInstance(info, 0);
            if (infoBean!= null && infoBean.getServiceName() != null) {
                result.add(infoBean);
            }
        }
        return result;
    }

    public MInstanceInfoBean getInstanceInfoById(String instanceId) {
        InstanceInfo info = this.clusterMiddleware.getInstanceInfoById(instanceId);
        if (info == null) {
            return null;
        } else {
            return this.transformInstance(info, 0);
        }
    }

    public MInstanceInfoBean getInstanceInfoByIp(String ipAddr) {
        InstanceInfo baseInfo = this.clusterMiddleware.getInstanceInfoByIpAndPort(ipAddr);
        if (baseInfo ==null) {
            return null;
        } else {
            return this.transformInstance(baseInfo, 0);
        }
    }

    /**
     * collect necessary information so we can build a MInstanceInfoBean from InstanceInfo
     * @param instanceInfo
     * @return
     */
    public MInstanceInfoBean transformInstance(InstanceInfo instanceInfo, int backwardPort) {
        MInstanceInfoBean instanceInfoBean = new MInstanceInfoBean();
        instanceInfoBean.setRegistryId(instanceInfo.getId().replace("'",""));
        instanceInfoBean.setIp(instanceInfo.getIPAddr().replace("'",""));
        instanceInfoBean.setPort(instanceInfo.getPort()==0?backwardPort:instanceInfo.getPort());
        if (!dockerManager.checkIfDockerRunning(instanceInfo.getIPAddr())) {
            return instanceInfoBean;
        }
        instanceInfoBean.setDockerInfo(dockerManager.getDockerInfoByIpAddr(instanceInfo.getIPAddr()));
        instanceInfoBean.setServiceName(instanceInfo.getAppName().replace("'",""));
        instanceInfoBean.setServiceVersion(instanceInfo.getMetadata().get(MConfig.MCLUSTER_SVC_VER_NAME).replace("'",""));
        return instanceInfoBean;
    }

    public List<InstanceInfo> getInstanceInfoList1(){
        return this.clusterMiddleware.getInstanceInfoList();
    }


    /*
     * Find all MGateway instances in the cluster
     */
    public List<InstanceInfo> getAllGatewayInstance() {
        List<InstanceInfo> resultInfoList = new ArrayList<>();
        List<InstanceInfo> infoList = this.clusterMiddleware.getInstanceInfoList();
        for (InstanceInfo info : infoList) {
            if (info.getAppName().equalsIgnoreCase("mgateway")) {
                resultInfoList.add(info);
            }
        }
        return resultInfoList;
    }

    public void depoly(MDeployPodRequest mDeployPodRequest) {
        try {
            V1Pod pod = dockerManager.deployInstanceOnNode(
                    mDeployPodRequest.getNodeId(),
                    mDeployPodRequest.getUniqueId(),
                    mDeployPodRequest.getServiceName(),
                    mDeployPodRequest.getImageUrl()
            );
            logger.info("Job " + mDeployPodRequest.getId() + " dispatched");
        } catch (Exception e) {
            logger.warn(String.format(
                    "Failed to notify job %s to MServer. Please check the connection to MServer",
                    mDeployPodRequest.getId()
            ));
        }
    }


    public Map<String,String> getAllNodeLabel(){
        return dockerManager.getAllnode();
    }
}
