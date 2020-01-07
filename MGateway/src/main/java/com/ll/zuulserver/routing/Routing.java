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
import java.net.URI;
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
        String version = request.getHeader("version");
        System.out.println("请求头部:" + version);
        String requestURI = request.getRequestURI();  // uri
        System.out.println("请求的路径为 : " + requestURI );
        String servicename =  requestURI.split("/")[1];
//        Route rout = routeLocator.getMatchingRoute(requestURI);  // 路由信息
//        System.out.println(rout);
        List<ServiceInstance> serviceInstances = MGetExample.getExammpleByVersion(discoveryClient,version, servicename);   // 是这个版本的信息的所有实例
        ServiceInstance getServiceInstance = serviceInstances.get(new Random().nextInt(serviceInstances.size()));
        String getUri = getServiceInstance.getUri().toString();
        System.out.println("获得的实例为：" + getUri);
        try{
            ctx.setRouteHost(new URI(getUri+"/").toURL());
        }catch (Exception e){
            e.printStackTrace();
        }
        ctx.put(FilterConstants.REQUEST_URI_KEY, requestURI.replaceFirst("/"+servicename+"/",""));
        return null;
    }
}
