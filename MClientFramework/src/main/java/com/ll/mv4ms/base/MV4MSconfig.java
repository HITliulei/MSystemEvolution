package com.ll.mv4ms.base;

import com.ll.mv4ms.utils.EurekaUtils;
import com.ll.mv4ms.utils.MSendByDependenvy;
import com.ll.mv4ms.utils.MSendRequest;
import com.netflix.appinfo.ApplicationInfoManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Lei
 * @Date 2020/3/16 13:36
 * @Version 1.0
 */

@Component
public class MV4MSconfig extends WebMvcConfigurerAdapter {

    @LoadBalanced
    @Bean
    public RestTemplate getRe(){
        return new RestTemplate();
    }

    @Bean
    public MSendRequest MSendRequest(){
        return new MSendRequest();
    }
    @Bean
    public MSendByDependenvy mSendByDependenvy(){
        return new MSendByDependenvy();
    }

    @Qualifier("eurekaApplicationInfoManager")
    @Autowired
    private ApplicationInfoManager applicationInfoManager;

    @Value("${mvf4ms.version}")
    private String version;

    @PostConstruct
    public void init(){
        Map<String,String> map = new HashMap<>();
        map.put("serviceVersion",version);
        applicationInfoManager.registerAppMetadata(map);
        EurekaUtils.initInfoManager(applicationInfoManager);
    }

    /**
     * 处理AJAX请求跨域的问题
     */
    static final String ORIGINS[] = new String[] { "GET", "POST", "PUT", "DELETE" };
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowCredentials(true).allowedMethods(ORIGINS)
                .maxAge(3600);
    }
}
