package com.ll.mv4ms.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nullable;


/**
 * Created by Lei on 2019/12/28 18:19
 */
@Component
public class MSendRequest {
    private static Logger logger= LogManager.getLogger(MSendRequest.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${spring.application.name}")
    private String serviceName;
    @Value("${mvf4ms.version}")
    private String serviceVersion;

    // 携带请求微服务的版本信息
    public <T> T sendRequest(String serviceRequestUrl, String version, Class<T> returnClass, RequestMethod method){
        return sendRequest(serviceRequestUrl, version, returnClass,method, null);
    }
    public <T> T sendRequest(String serviceRequestUrl, String version, Class<T> returnClass, RequestMethod method, @Nullable Object paramer){
        serviceRequestUrl = serviceRequestUrl.replace("MGateWay","magteway/mgateway");
        ResponseEntity<T> returnEntity = null;
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("requestVersion", version);
        requestHeaders.add("serviceName",this.serviceName);

        HttpEntity<Object> paramers = new HttpEntity<>(paramer,requestHeaders);
        switch (method){
            case GET:
                logger.info("get方式请求");
                returnEntity = restTemplate.exchange(serviceRequestUrl, HttpMethod.GET, paramers,returnClass);
                break;
            case POST:
                logger.info("post方式请求");
                returnEntity = restTemplate.postForEntity(serviceRequestUrl,paramers,returnClass);
                break;
            case PUT:
                logger.info("put方式请求");
                returnEntity = restTemplate.exchange(serviceRequestUrl, HttpMethod.PUT, paramers,returnClass);
                break;
            case DELETE:
                logger.info("delete方式请求");
                returnEntity = restTemplate.exchange(serviceRequestUrl, HttpMethod.DELETE, paramers,returnClass);
                break;
            default:
                logger.warn("暂时无这种请求的方式");
                break;
        }
        System.out.println(returnEntity.getBody());
        return returnEntity.getBody();
    }
    // 不携带请求微服务的版本信息
    public <T> T sendRequest(String serviceRequestUrl, Class<T> returnClass, RequestMethod method){
        return sendRequest(serviceRequestUrl,returnClass,method,null);
    }
    public <T> T sendRequest(String serviceRequestUrl, Class<T> returnClass, RequestMethod method, @Nullable Object paramer){
        serviceRequestUrl = serviceRequestUrl.replace("MGateWay","magteway/mgateway");
        ResponseEntity<T> returnEntity = null;
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("serviceName",this.serviceName);
        requestHeaders.add("serviceVersion",this.serviceVersion);
        HttpEntity<Object> paramers = new HttpEntity<>(paramer,requestHeaders);
        switch (method){
            case GET:
                logger.info("get方式请求");
//                returnEntity = restTemplate.exchange(serviceRequestURL, HttpMethod.GET, new HttpEntity<>(paramer,requestHeaders),returnClass);
                returnEntity = restTemplate.exchange(serviceRequestUrl, HttpMethod.GET, paramers,returnClass);
                break;
            case POST:
                logger.info("post方式请求");
                returnEntity = restTemplate.postForEntity(serviceRequestUrl,paramers,returnClass);
//                returnEntity = restTemplate.exchange(serviceRequestURL, HttpMethod.POST, paramers, returnClass);
                break;
            case PUT:
                logger.info("put方式请求");
                returnEntity = restTemplate.exchange(serviceRequestUrl, HttpMethod.PUT, new HttpEntity<>(paramer,requestHeaders),returnClass);
                break;
            case DELETE:
                logger.info("delete方式请求");
                returnEntity = restTemplate.exchange(serviceRequestUrl, HttpMethod.DELETE, new HttpEntity<>(paramer,requestHeaders),returnClass);
                break;
            default:
                logger.warn("暂时无这种请求的方式");
                break;
        }
        System.out.println(returnEntity.getBody());
        return returnEntity.getBody();
    }

}
