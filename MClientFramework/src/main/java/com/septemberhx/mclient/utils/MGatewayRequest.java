package com.septemberhx.mclient.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Lei on 2019/12/30 15:57
 */
public class MGatewayRequest {

    @Autowired
    private RestTemplate restTemplate;

    public <T> T sendRequest(String serviceRequestURL,String version,Class<T> responseType){
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("version", version);
        ResponseEntity<T> exchange = restTemplate.exchange(serviceRequestURL, HttpMethod.GET, new HttpEntity<String>(requestHeaders),responseType);
        System.out.println("返回值");
        System.out.println(exchange.getBody());
        return exchange.getBody();
    }

    public <T> T sendRequest(String serviceRequestURL,Class<T> responseType){
        return null;
    }


}
