package com.septemberhx.mclient.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Lei on 2019/12/30 15:57
 */
public class MGatewayRequest {
    private Logger logger = LogManager.getLogger(MGatewayRequest.class);

    @Autowired
    private RestTemplate restTemplate;

    public <T> T sendRequest(String serviceRequestURL, String version, Class<T> returnClass, RequestMethod method){
        return sendRequest(serviceRequestURL, version, returnClass,method, null);
    }
    public <T> T sendRequest(String serviceRequestURL, String version, Class<T> returnClass, RequestMethod method,@Nullable Object paramer){
        ResponseEntity<T> returnEntity = null;
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("version", version);
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
        if (returnEntity == null) {
            throw new RuntimeException("Error when sendRequest in MGatewayRequest");
        }
        System.out.println(returnEntity.getBody());
        return returnEntity.getBody();
    }

    public <T> T sendRequest(String serviceRequestURL,Class<T> responseType){
        return null;
    }


}
