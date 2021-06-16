package com.septemberhx.agent.controller;

import com.netflix.appinfo.InstanceInfo;
import com.septemberhx.agent.utils.MClientUtils;
import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.bean.MTimeIntervalBean;
import com.septemberhx.common.bean.MUserRequestBean;
import com.septemberhx.common.bean.agent.*;
import com.septemberhx.common.base.user.MUser;
import com.septemberhx.common.base.user.MUserDemand;
import com.septemberhx.common.bean.gateway.MDepRequestCacheBean;
import com.septemberhx.common.bean.gateway.MDepRequestCacheListBean;
import com.septemberhx.common.utils.MRequestUtils;
import com.septemberhx.common.utils.MUrlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@RestController
@EnableAutoConfiguration
@RequestMapping("/magent")
public class MAgentController {

    private static Logger logger = LogManager.getLogger(MAgentController.class);


    @Value("${mvf4ms.center.ip}")
    private String serverIpAddr;

    @Value("${mvf4ms.center.port}")
    private Integer serverPort;

    @Autowired
    private MClientUtils clientUtils;

    // use it to record who send the instance info to server
    private static Map<String, MInstanceInfoBean> instanceInfoBeanMap = new HashMap<>();

    public MAgentController() {
    }

    @ResponseBody
    @RequestMapping(path = "/deleteInstance", method = RequestMethod.DELETE)
    public void deleteInstance(@RequestParam("dockerInstanceId") String instanceId) {
        MClientUtils.deleteInstanceById(instanceId);
    }

    @ResponseBody
    @RequestMapping(path = "/deploy", method = RequestMethod.POST)
    public void deploy(@RequestBody MDeployPodRequest mDeployPodRequest) {
        logger.info("要部署的信息为:" + mDeployPodRequest);
        this.clientUtils.depoly(mDeployPodRequest);
    }

    @ResponseBody
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public void register(@RequestBody MDeployPodRequest mDeployPodRequest){
        boolean result = false;
        int count = 0;
        while(!result) {
            try{
                System.out.println("sleep 3 second");
                Thread.sleep(3000);
                List<InstanceInfo> list = clientUtils.getInstanceInfoList1();
                for(InstanceInfo info:list){
                    if (info.getInstanceId().split(":")[0].equalsIgnoreCase(mDeployPodRequest.getUniqueId()) && info.getAppName().equalsIgnoreCase(mDeployPodRequest.getServiceName())){
                        MInstanceInfoBean infoBean = this.clientUtils.transformInstance(info, info.getPort());
                        logger.info("发送MInstanceInfoBean" + infoBean);
                        HttpHeaders requestHeaders = new HttpHeaders();
                        HttpEntity<Object> paramers = new HttpEntity<>(infoBean,requestHeaders);
                        new RestTemplate().postForLocation("http://"+serverIpAddr+":"+serverPort+"/cluster/reportInstanceInfo",paramers);
                        result = true;
                        break;
                    }
                }
                if(count >= 15){
                    result = true;
                }else{
                    count = count +1;
                }
            }catch(InterruptedException ie){
                ie.printStackTrace();
            }
        }
    }

    @GetMapping("/getAllNodeLable")
    public Map<String,String> getallNodeLabel(){
        return this.clientUtils.getAllNodeLabel();
    }

    @ResponseBody
    @RequestMapping(path = "/registered", method = RequestMethod.POST)
    public void instanceRegistered(@RequestBody MInstanceRegisterNotifyRequest registerNotifyRequest) {
        InstanceInfo instanceInfo = registerNotifyRequest.getInstanceInfo();
        logger.info("注册中心返回信息"+ instanceInfo);
        // get instance info
        MInstanceInfoBean infoBean = this.clientUtils.transformInstance(instanceInfo, registerNotifyRequest.getPort());
        logger.info("得到的完整的服务信息" + infoBean);
        if (infoBean == null) {
            return;
        }
        URI serverLoadUri = MUrlUtils.getMServerLoadInstanceInfoUri(this.serverIpAddr, this.serverPort);
        try {
            MRequestUtils.sendRequest(serverLoadUri, infoBean, null, RequestMethod.POST);
//            this.clientUtils.notifyDeployJobFinished(infoBean);
        } catch (Exception e) {
            logger.debug(e);
            logger.warn("Failed to notify server with data in MAgentController::instanceRegistered");
        }
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
