package com.septemberhx.agent.controller;

import com.netflix.appinfo.InstanceInfo;
import com.septemberhx.agent.config.MAgentConfig;
import com.septemberhx.agent.utils.ElasticSearchUtils;
import com.septemberhx.agent.utils.MClientUtils;
import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.bean.MTimeIntervalBean;
import com.septemberhx.common.bean.MUserRequestBean;
import com.septemberhx.common.bean.agent.*;
import com.septemberhx.common.base.user.MUser;
import com.septemberhx.common.base.user.MUserDemand;
import com.septemberhx.common.bean.gateway.MDepRequestCacheBean;
import com.septemberhx.common.bean.gateway.MDepRequestCacheListBean;
import com.septemberhx.common.bean.gateway.MDepRequestCountBean;
import com.septemberhx.common.bean.gateway.MDepRequestCountListBean;
import com.septemberhx.common.config.MConfig;
import com.septemberhx.common.utils.MRequestUtils;
import com.septemberhx.common.utils.MUrlUtils;
import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@EnableAutoConfiguration
@RequestMapping("/magent")
public class MAgentController {

    private static Logger logger = LogManager.getLogger(MAgentController.class);

    @Autowired
    private MClientUtils clientUtils;

    @Autowired
    private MAgentConfig agentConfig;

    private RestHighLevelClient esClient;
    // use it to record who send the instance info to server
    private static Map<String, MInstanceInfoBean> instanceInfoBeanMap = new HashMap<>();
    public MAgentController() {
    }

    @PostConstruct
    public void init() throws IOException {
        logger.info("Elasticsearch: " + this.agentConfig.getElasticsearch().getIp() + ":" + this.agentConfig.getElasticsearch().getIp());
        this.esClient = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(this.agentConfig.getElasticsearch().getIp(), this.agentConfig.getElasticsearch().getPort())
                )
        );
    }

    @ResponseBody
    @RequestMapping(path = "/doRequest", method = RequestMethod.POST)
    public MResponse doRequest(@RequestBody MUserRequestBean requestBean) {
        URI uri = MUrlUtils.getMServerDoRequestUri(this.agentConfig.getCenter().getIp(), this.agentConfig.getCenter().getPort());
        return MRequestUtils.sendRequest(uri, requestBean, MResponse.class, RequestMethod.POST);
    }

    @ResponseBody
    @RequestMapping(path = "/fetchRequestUrl", method = RequestMethod.POST)
    public String fetchRequestUrl(@RequestBody MUserDemand userDemand) {
        String result = null;
        try {
            URI requestUri = MUrlUtils.getMServerFetchRequestUrl(this.agentConfig.getCenter().getIp(), this.agentConfig.getCenter().getPort());
            result = MRequestUtils.sendRequest(requestUri, userDemand, String.class, RequestMethod.POST);
        } catch (Exception e) { }
        return result;
    }

    @ResponseBody
    @RequestMapping(path = "/updateGateways", method = RequestMethod.POST)
    public void updateGateway(@RequestBody MUpdateCacheBean cacheBean) {
        for (InstanceInfo info : this.clientUtils.getAllGatewayInstance()) {
            URI uri = MUrlUtils.getMGatewayUpdateUri(info.getIPAddr(), info.getPort());
            System.out.println(uri.toString());
            MRequestUtils.sendRequest(uri, cacheBean, null, RequestMethod.POST);
        }
    }

    @ResponseBody
    @PostMapping(path = "/fetchDepRequests")
    public MDepRequestCacheListBean getRequestsBetween(@RequestBody MTimeIntervalBean timeIntervalBean) {
        List<MDepRequestCacheBean> resultList = new ArrayList<>();
        for (InstanceInfo info : this.clientUtils.getAllGatewayInstance()) {
            URI uri = MUrlUtils.getMGatewayFetchRequestsUri(info.getIPAddr(), info.getPort());
            MDepRequestCacheListBean cacheListBean =
                MRequestUtils.sendRequest(uri, timeIntervalBean, MDepRequestCacheListBean.class, RequestMethod.POST);
            resultList.addAll(cacheListBean.getRequestList());
        }
        return new MDepRequestCacheListBean(resultList);
    }

    @ResponseBody
    @PostMapping(path = "/fetchDepRequestCount")
    public MDepRequestCountListBean getRequestNumBetween(@RequestBody MTimeIntervalBean timeIntervalBean) {
        List<MDepRequestCountBean> resultList = new ArrayList<>();
        for (InstanceInfo info : this.clientUtils.getAllGatewayInstance()) {
            URI uri = MUrlUtils.getRemoteUri(info.getIPAddr(), info.getPort(), MConfig.MGATEWAY_FETCH_REQUEST_NUMBER);
            MDepRequestCountBean cacheListBean =
                    MRequestUtils.sendRequest(uri, timeIntervalBean, MDepRequestCountBean.class, RequestMethod.POST);
            resultList.add(cacheListBean);
        }
        return new MDepRequestCountListBean(resultList);
    }

    @ResponseBody
    @RequestMapping(path = "/allUser", method = RequestMethod.POST)
    public MAllUserBean getAllUser() {
        List<MUser> allUserList = new ArrayList<>();
        for (InstanceInfo info : this.clientUtils.getAllGatewayInstance()) {
            URI uri = MUrlUtils.getMGatewayAllUserUri(info.getIPAddr(), info.getPort());
            System.out.println(uri.toString());
            MAllUserBean userListBean = MRequestUtils.sendRequest(uri, null, MAllUserBean.class, RequestMethod.POST);
            if (userListBean != null) {
                allUserList.addAll(userListBean.getAllUserList());
            }
        }
        return new MAllUserBean(allUserList);
    }

    @ResponseBody
    @RequestMapping(path = "/instanceInfoList", method = RequestMethod.GET)
    public MInstanceInfoResponse getInstanceInfoList() {
        MInstanceInfoResponse response = new MInstanceInfoResponse();
        response.setInfoBeanList(this.clientUtils.getInstanceInfoList());
        return response;
    }

    @ResponseBody
    @RequestMapping(path = "/deleteInstance", method = RequestMethod.GET)
    public void deleteInstance(@RequestParam("dockerInstanceId") String instanceId) {
        MClientUtils.deleteInstanceById(instanceId);
    }

    @ResponseBody
    @RequestMapping(path = "/remoteuri", method = RequestMethod.POST)
    public URI getRemoteUri(@RequestBody MGetRemoteUriRequest remoteUriRequest) {
        URI serverRemoteUri = MUrlUtils.getMServerRemoteUri(this.agentConfig.getCenter().getIp(), this.agentConfig.getCenter().getPort());
        return MRequestUtils.sendRequest(serverRemoteUri, remoteUriRequest, URI.class, RequestMethod.POST);
    }

    @ResponseBody
    @RequestMapping(path = "/setRestInfo", method = RequestMethod.POST)
    public void setRemoteUri(@RequestBody MSetRestInfoRequest mSetRestInfoRequest) {
        MInstanceInfoBean infoBean = this.clientUtils.getInstanceInfoById(mSetRestInfoRequest.getInstanceId());
        MClientUtils.sendRestInfo(
                MUrlUtils.getMClusterSetRestInfoUri(infoBean.getIp(), infoBean.getPort()),
                mSetRestInfoRequest.getRestInfoBean());
    }

    @ResponseBody
    @RequestMapping(path = "/deploy", method = RequestMethod.POST)
    public void deploy(@RequestBody MDeployPodRequest mDeployPodRequest) {
        this.clientUtils.depoly(mDeployPodRequest);
    }

    @ResponseBody
    @RequestMapping(path = "/registered", method = RequestMethod.POST)
    public void instanceRegistered(@RequestBody MInstanceRegisterNotifyRequest registerNotifyRequest) {
        InstanceInfo instanceInfo = registerNotifyRequest.getInstanceInfo();
//        System.out.println(instanceInfo.getAppName() + "|" + instanceInfo.getInstanceId() + "|" + instanceInfo.getIPAddr() + ":" + instanceInfo.getPort());
        MInstanceInfoBean infoBean = this.clientUtils.transformInstance(instanceInfo, registerNotifyRequest.getPort());
        if (infoBean == null) {
            return;
        }

        URI serverLoadUri = MUrlUtils.getMServerLoadInstanceInfoUri(this.agentConfig.getCenter().getIp(), this.agentConfig.getCenter().getPort());

        try {
//            logger.info(infoBean.toString());
            MRequestUtils.sendRequest(serverLoadUri, infoBean, null, RequestMethod.POST);
            this.clientUtils.notifyDeployJobFinished(infoBean);
        } catch (Exception e) {
            logger.info(e);
            logger.warn("Failed to notify server with data in MAgentController::instanceRegistered");
        }
    }

    @RequestMapping(path = "/setApiContinueStatus", method = RequestMethod.POST)
    public void setApiContinueStatus(@RequestBody MS2CSetApiCStatus ms2CSetApiCStatus) {
        MInstanceInfoBean infoBean = this.clientUtils.getInstanceInfoById(ms2CSetApiCStatus.getInstanceId());
        MRequestUtils.sendRequest(MUrlUtils.getMClientSetApiCStatus(infoBean.getIp(), infoBean.getPort()), ms2CSetApiCStatus.getApiContinueRequest(), null, RequestMethod.POST);
    }

    @RequestMapping(path = "/fetchLogsBetweenTime", method = RequestMethod.POST)
    public MFetchLogsResponse fetchLogsBetweenTime(@RequestBody MFetchLogsBetweenTimeRequest request) {
        MFetchLogsResponse response = new MFetchLogsResponse();
        response.setLogList(ElasticSearchUtils.getLogsBetween(
                this.esClient,
                new String[]{"logstash-*"},
                new DateTime(request.getStartTime()),
                new DateTime(request.getEndTime())
        ));
        return response;
    }

    synchronized private boolean checkIfInstanceInfoHasSend(MInstanceInfoBean infoBean) {
        return instanceInfoBeanMap.containsKey(infoBean.getRegistryId()) && instanceInfoBeanMap.get(infoBean.getRegistryId()).equals(infoBean);
    }

    synchronized private void recordInstanceInfo(MInstanceInfoBean infoBean) {
        if (instanceInfoBeanMap.size() > 1000) {
            instanceInfoBeanMap.clear();
        }

        instanceInfoBeanMap.put(infoBean.getRegistryId(), infoBean);
    }
}
