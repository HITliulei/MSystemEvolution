package com.ll.zuulserver.routing;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Random;

/**
 * Created by Lei on 2019/12/20 12:59
 */
public class Routing extends ZuulFilter {


    @Autowired
    private DiscoveryClient discoveryClient;


    private RouteLocator routeLocator;

    public Routing(RouteLocator routeLocator){
        this.routeLocator = routeLocator;
    }

    @Override
    public String filterType() {
        return FilterConstants.ROUTE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        //可以根据业务要求，过滤相关路由
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletResponse response = ctx.getResponse();
        HttpServletRequest request = ctx.getRequest();
        System.out.println("请求头部");
        String version = request.getHeader("version");
        String requestURI = request.getRequestURI();  // uri
        System.out.println("请求的路径为 : " + requestURI );
        String servicename =  requestURI.split("/")[1];
//        Route rout = routeLocator.getMatchingRoute(requestURI);  // 路由信息
//        System.out.println(rout);
        List<ServiceInstance> serviceInstances = MGetExample.getExammpleByVersion(discoveryClient,version, servicename);   // 是这个版本的信息的所有实例
        ServiceInstance getServiceInstance = serviceInstances.get(new Random().nextInt(serviceInstances.size()));
        System.out.println("得到的serviceinstance的 id " + getServiceInstance.getUri().toString());
        String changeUri = getServiceInstance.getUri().toString() + requestURI.replaceFirst("/"+servicename,"");
        System.out.println("更改后的请求路径为 " + changeUri);
        try{
            response.sendRedirect(changeUri);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
