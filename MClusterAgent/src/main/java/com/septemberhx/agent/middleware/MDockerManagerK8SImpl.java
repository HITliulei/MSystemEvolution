package com.septemberhx.agent.middleware;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.septemberhx.common.bean.agent.MDockerInfoBean;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.apis.ExtensionsV1beta1Api;
import io.kubernetes.client.models.*;
import io.kubernetes.client.util.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.septemberhx.agent.utils.MClientUtils.readPodYaml;
import static com.septemberhx.common.config.MConfig.K8S_NAMESPACE;

public class MDockerManagerK8SImpl implements MDockerManager {

    private ApiClient client;
    private CoreV1Api coreV1Api;
    private ExtensionsV1beta1Api extensionsV1beta1Api;
    private static Logger logger = LogManager.getLogger(MDockerManagerK8SImpl.class);

    public MDockerManagerK8SImpl(String k8sClientUrl) {
        this.initConnection(k8sClientUrl);
    }

    public MDockerManagerK8SImpl() {
        this.initConnection(null);
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
//            Thread watchThread = new MWatchPodStatusThread(k8sClientUrl);
//            watchThread.start();
//            watchPodStatus();
        } catch (IOException e) {
            logger.debug(e);
        }
    }

    @Override
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
    @Override
    public V1Pod deployInstanceOnNode(String nodeId, String instanceId, String serviceName, String imageUrl) {
        V1Pod podBody = readPodYaml("template");
        // fill the node selector
        if (podBody.getSpec().getNodeSelector() == null) {
            podBody.getSpec().setNodeSelector(new HashMap<>());
        }
        podBody.getSpec().getNodeSelector().put("node", nodeId);
        podBody.getMetadata().getLabels().put("app", serviceName.toLowerCase());
        podBody.getSpec().getContainers().get(0).setName(serviceName.toLowerCase());
        podBody.getSpec().getContainers().get(0).setImage(imageUrl);

        if (instanceId != null) {
            podBody.getMetadata().setName(instanceId);
        }
        logger.info("要部署的实例的yml为" + podBody.toString());

        V1Pod resultPod = null;
        try {
            resultPod = coreV1Api.createNamespacedPod(K8S_NAMESPACE, podBody, null, null, null);
        } catch (ApiException e) {
            logger.debug(e);
        }
        return resultPod;
    }

    @Override
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
            logger.debug(e);
        } catch (Exception e) {
            ;
        }
        return true;
    }

    @Override
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
                    Map<String, String> node = getAllnode();
                    infoBean.setNodeLabel(node.get(item.getSpec().getNodeName()));
                }
            }
        } catch (ApiException e) {
            logger.debug(e);
        }
        return infoBean;
    }


    @Override
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
            logger.debug(e);
        }
        return false;
    }



}
