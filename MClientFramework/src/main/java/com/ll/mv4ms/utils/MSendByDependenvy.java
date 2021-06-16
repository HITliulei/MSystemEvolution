package com.ll.mv4ms.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nullable;

/**
 * @Author Lei
 * @Date 2020/4/9 16:53
 * @Version 1.0
 */
public class MSendByDependenvy {
    private static Logger logger= LogManager.getLogger(MSendByDependenvy.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${spring.application.name}")
    private String serviceName;
    @Value("${mvf4ms.version}")
    private String serviceVersion;

    public <T> T sendRequest(String dependencyName, String dependencyId, Class<T> returnClass, RequestMethod method){
        return sendRequest(dependencyName,dependencyId,returnClass,method,null);
    }
    public <T> T sendRequest(String dependencyName, String dependencyId, Class<T> returnClass, RequestMethod method, @Nullable Object paramer){
        String serviceRequestURL = "http://magteway/mgateway"+dependencyName + "/" +dependencyId;
        ResponseEntity<T> returnEntity = null;
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("serviceName",this.serviceName);
        requestHeaders.add("serviceVersion",this.serviceVersion);
        HttpEntity<Object> paramers = new HttpEntity<>(paramer,requestHeaders);
        switch (method){
            case GET:
                logger.info("get方式请求");
//                returnEntity = restTemplate.exchange(serviceRequestURL, HttpMethod.GET, new HttpEntity<>(paramer,requestHeaders),returnClass);
                returnEntity = restTemplate.exchange(serviceRequestURL, HttpMethod.GET, paramers,returnClass);
                break;
            case POST:
                logger.info("post方式请求");
                returnEntity = restTemplate.postForEntity(serviceRequestURL,paramers,returnClass);
//                returnEntity = restTemplate.exchange(serviceRequestURL, HttpMethod.POST, paramers, returnClass);
                break;
            case PUT:
                logger.info("put方式请求");
                returnEntity = restTemplate.exchange(serviceRequestURL, HttpMethod.PUT, new HttpEntity<>(paramer,requestHeaders),returnClass);
                break;
            case DELETE:
                logger.info("delete方式请求");
                returnEntity = restTemplate.exchange(serviceRequestURL, HttpMethod.DELETE, new HttpEntity<>(paramer,requestHeaders),returnClass);
                break;
            default:
                logger.warn("暂时无这种请求的方式");
                break;
        }
        System.out.println(returnEntity.getBody());
        return returnEntity.getBody();
    }
}
