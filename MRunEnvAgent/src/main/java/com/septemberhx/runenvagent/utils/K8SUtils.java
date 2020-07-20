package com.septemberhx.runenvagent.utils;

import com.netflix.appinfo.InstanceInfo;
import com.septemberhx.common.bean.agent.MDockerInfoBean;
import com.septemberhx.common.bean.agent.MInstanceInfoBean;
import com.septemberhx.common.config.MConfig;
import com.septemberhx.runenvagent.config.MAgentConfig;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.apis.ExtensionsV1beta1Api;
import io.kubernetes.client.models.V1Node;
import io.kubernetes.client.models.V1NodeList;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.util.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.septemberhx.common.config.MConfig.K8S_NAMESPACE;
import static com.septemberhx.runenvagent.utils.CommonUtil.readPodYaml;

/**
 * @Author Lei
 * @Date 2020/7/20 19:45
 * @Version 1.0
 */

public class K8SUtils {

    private static Logger logger = LogManager.getLogger(K8SUtils.class);

    private ApiClient client;
    private CoreV1Api coreV1Api;
    private ExtensionsV1beta1Api extensionsV1beta1Api;


    @Autowired
    private MAgentConfig mAgentConfig;
     @Bean
    public K8SUtils getK8sUtils(){
        if(this.mAgentConfig.getCluster().getK8sClientUrl() == null){
            return new K8SUtils();
        }else{
            return new K8SUtils(this.mAgentConfig.getCluster().getK8sClientUrl());
        }
    }


    public K8SUtils(){
        this.initConnection(null);
    }


    public K8SUtils(String k8sClientUrl){
        this.initConnection(k8sClientUrl);
    }


    private void initConnection(String k8sClientUrl) {
        try {
            if (k8sClientUrl == null) {
                this.client = Config.defaultClient();
            } else {
                this.client = Config.fromUrl(k8sClientUrl);
            }
            this.client.getHttpClient().setReadTimeout(0, TimeUnit.SECONDS);
            Configuration.setDefaultApiClient(client);
            this.coreV1Api = new CoreV1Api(this.client);
            this.extensionsV1beta1Api = new ExtensionsV1beta1Api(this.client);
        } catch (IOException e) {
            logger.info(e);
        }
    }

    /**
     * deploy one instance in cluster
     * @param nodeId worker node-label
     * @param instanceId pod Id
     * @param serviceName service Name
     * @param imageUrl they service docker image url
     * @return V1Pod
     */
    public V1Pod deployInstanceOnNode(String nodeId, String instanceId, String serviceName, String imageUrl) {
        V1Pod podBody = readPodYaml("template");

        // fill the node selector
        if (podBody.getSpec().getNodeSelector() == null) {
            podBody.getSpec().setNodeSelector(new HashMap<>());
        }
        podBody.getSpec().getNodeSelector().put("node", nodeId);
        podBody.getMetadata().getLabels().put("app", serviceName);
        podBody.getSpec().getContainers().get(0).setName(serviceName);
        podBody.getSpec().getContainers().get(0).setImage(imageUrl);

        if (instanceId != null) {
            podBody.getMetadata().setName(instanceId);
        }

        V1Pod resultPod = null;
        try {
            resultPod = coreV1Api.createNamespacedPod(K8S_NAMESPACE, podBody, null, null, null);
        } catch (ApiException e) {
            logger.info(e);
        }
        return resultPod;
    }

    /**
     * delete instance in cluster by instanceId
     * @param instanceId instance ID
     * @return whether delete successfully
     */
    public boolean deleteInstanceById(String instanceId) {
        try {
            V1PodList podList = this.coreV1Api.listNamespacedPod(K8S_NAMESPACE, true, null,
                    null, null, null, null,
                    null, null, null);
            boolean ifExists = false;
            for (V1Pod pod : podList.getItems()) {
                if (pod.getMetadata().getName().equals(instanceId)) {
                    ifExists = true;
                    break;
                }
            }
            if (!ifExists) {
                return false;
            }

            // Because we control how the pods are deployed, we know that the pod we try to delete is deployed as a pod
            // So we can just delete it, and it will not be reborn.
            // In the future, we need to take a more elegant way to do this work.
            this.coreV1Api.deleteNamespacedPod(
                    instanceId,
                    K8S_NAMESPACE,
                    null,
                    null,
                    null,
                    null,
                    null,
                    "Foreground"
            );
            logger.info(String.format("Pod %s was deleted.", instanceId));
        } catch (IllegalStateException e) {
            ;
        } catch (ApiException e) {
            logger.info(e);
        } catch (Exception e) {
            logger.info(e);
        }
        return true;
    }


    /**
     * get all node in K8S cluster
     * @return  node label
     */
    public Map<String, String> getAllnode(){
        Map<String, String> map = new HashMap<>();
        V1NodeList v1NodeList = null;
        try {
            v1NodeList = this.coreV1Api.listNode(null,
                    null, null, null, null, null,
                    null, null, null);
            List<V1Node> items = v1NodeList.getItems();
            for(V1Node v1Node:items){
                Map<String, String> labels = v1Node.getMetadata().getLabels();
                for(String string: labels.keySet()){
                    if(string.equals("node")){
                        map.put(v1Node.getMetadata().getName(), labels.get(string));
                        break;
                    }
                }
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return map;
    }


    /**
     *  check if this docker running in cluster
     * @param ipAddr the ip Addr of this docker
     * @return true/false
     */
    public boolean checkIfDockerRunning(String ipAddr) {
        try {
            V1PodList list = this.coreV1Api.listNamespacedPod(K8S_NAMESPACE, null, null, null, null, null, null, null, null, null);
            for (V1Pod item : list.getItems()) {
                if (ipAddr.equals(item.getStatus().getPodIP())) {
                    if (item.getStatus().getPhase().equals("Running")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            logger.info(e);
        }
        return false;
    }

    /**
     *  check if this docker running in cluster
     * @param ipAddr the ip Addr of this docker
     * @return true/false
     */
    public MDockerInfoBean getDockerInfoByIpAddr(String ipAddr) {
        MDockerInfoBean infoBean = null;
        try {
            infoBean = new MDockerInfoBean();
            V1PodList list = this.coreV1Api.listNamespacedPod(K8S_NAMESPACE, null, null, null, null, null, null, null, null, null);
            for (V1Pod item : list.getItems()) {
                if (item.getStatus() != null &&
                        "Running".equals(item.getStatus().getPhase()) && ipAddr.equals(item.getStatus().getPodIP())) {
                    infoBean.setHostIp(item.getStatus().getHostIP());
                    infoBean.setInstanceId(item.getMetadata().getName());
                }
            }
        } catch (ApiException e) {
            logger.info(e);
        }
        return infoBean;
    }


    /**
     * collect necessary information so we can build a MInstanceInfoBean from InstanceInfo
     * @param instanceInfo
     * @return
     */
    public MInstanceInfoBean transformInstance(InstanceInfo instanceInfo, int backwardPort) {
        MInstanceInfoBean instanceInfoBean = new MInstanceInfoBean();

        if (!instanceInfo.getMetadata().containsKey(MConfig.MCLUSTER_SVC_METADATA_NAME)
                || !instanceInfo.getMetadata().get(MConfig.MCLUSTER_SVC_METADATA_NAME).equals(
                MConfig.MCLUSTER_SVC_METADATA_VALUE)) {
            return null;
        }

        instanceInfoBean.setRegistryId(instanceInfo.getId());
        instanceInfoBean.setIp(instanceInfo.getIPAddr());

        int instancePort = instanceInfo.getPort();
        if (instancePort == 0) instancePort = backwardPort;

        instanceInfoBean.setPort(instancePort);

        instanceInfoBean.setClusterId(this.mAgentConfig.getCluster().getName());
        instanceInfoBean.setServiceName(instanceInfo.getAppName());
        instanceInfoBean.setVersion(instanceInfo.getMetadata().get(MConfig.MCLUSTER_SVC_VER_NAME));

        if (!this.checkIfDockerRunning(instanceInfo.getIPAddr())) {
            return instanceInfoBean;
        }
//        MClientInfoBean response = null;
//        try {
//            response = MRequestUtils.sendRequest(
//                    MUrlUtils.getMClusterAgentFetchClientInfoUri(instanceInfo.getIPAddr(), instancePort),
//                    null,
//                    MClientInfoBean.class,
//                    RequestMethod.GET
//            );
//        } catch (Exception e) {
//            return instanceInfoBean;
//        }
//
//        if (response == null) {
//            return instanceInfoBean;
//        }
//
//        instanceInfoBean.setParentIdMap(response.getParentIdMap());
//        instanceInfoBean.setApiMap(response.getApiMap());
//        instanceInfoBean.setMObjectIdMap(response.getMObjectIdSet());
        instanceInfoBean.setDockerInfo(this.getDockerInfoByIpAddr(instanceInfo.getIPAddr()));

        return instanceInfoBean;
    }

}
