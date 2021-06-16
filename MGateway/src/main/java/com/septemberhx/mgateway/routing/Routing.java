package com.septemberhx.mgateway.routing;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.septemberhx.common.bean.agent.MInstanceInfoBean;
import com.septemberhx.common.bean.gateway.MRequestUrl;
import com.septemberhx.common.service.MSvcVersion;
import com.septemberhx.common.utils.MUrlUtils;
import com.septemberhx.mgateway.client.ConnectToCenter;
import com.septemberhx.mgateway.client.ConnectToClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;
import java.util.Random;

/**
 * Created by Lei on 2019/12/20 12:59
 */
@Component
public class Routing extends ZuulFilter {

    @Autowired
    private ConnectToCenter connectToCenter;

    @Autowired
    private ConnectToClient connectToClient;


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
        System.out.println("网关运行");
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String hostIp = request.getRemoteAddr();
        System.out.println("请求的ip为 + " + hostIp);
        String serviceName = request.getHeader("serviceName");
        String serviceVersion = request.getHeader("serviceVersion");
        String requestVersion = request.getHeader("requestVersion");
        String requestUri = request.getRequestURI();
        System.out.println("请求的uri为" + requestUri);
        String requestURI = requestUri.replaceAll("/mgateway","");
        System.out.println("请求的uri为" + requestURI);
        String requestServicename =  requestURI.split("/")[1];
        if(ifServiceName(requestServicename)){
            System.out.println("路径请求");
            List<MInstanceInfoBean> mInstanceInfoBeans;
            if(requestVersion != null){
                System.out.println("请求头部:" + requestVersion);
                mInstanceInfoBeans = new MPathRquest().getExammpleByVersion(connectToCenter, connectToClient, serviceName+"_"+ MSvcVersion.fromStr(serviceVersion).toString(), requestVersion, requestServicename, hostIp);
            }else{
                mInstanceInfoBeans =  new MPathRquest().getExammpleByWithoutVersion(connectToCenter, connectToClient, serviceName,serviceVersion,requestURI, hostIp);
            }
            if(mInstanceInfoBeans.isEmpty()){
                return null;
            }
            MInstanceInfoBean getServiceInstance = mInstanceInfoBeans.get(new Random().nextInt(mInstanceInfoBeans.size()));
            System.out.println("路由得到的实例为" + getServiceInstance);
            try{
                URI uri = MUrlUtils.getRemoteUri(getServiceInstance.getIp(),getServiceInstance.getPort(),"/");
                ctx.setRouteHost(uri.toURL());
//                ctx.setRouteHost(new URI(getServiceInstance.getUri().toString()+"/").toURL());
            }catch (Exception e){
                e.printStackTrace();
            }
            ctx.put(FilterConstants.REQUEST_URI_KEY, requestURI.replaceFirst("/"+requestServicename+"/",""));
            connectToCenter.updateUseful(getServiceInstance);
            return null;
        }else{
            System.out.println("依赖请求");
            String name =  requestURI.split("/")[1];
            String id = requestURI.split("/")[2];
            MRequestUrl mRequestUrl = new MDependencyRequest().getInstanceInfoBean( name, id, hostIp, serviceName, serviceVersion,connectToClient, connectToCenter);
            try{
                URI uri = MUrlUtils.getRemoteUri(mRequestUrl.getIp(),Integer.parseInt(mRequestUrl.getPort()),"/");
                ctx.setRouteHost(uri.toURL());
//                ctx.setRouteHost(new URI(getServiceInstance.getUri().toString()+"/").toURL());
            }catch (Exception e){
                e.printStackTrace();
            }
            ctx.put(FilterConstants.REQUEST_URI_KEY, mRequestUrl.getInsterfaceName().replaceFirst("/",""));
            return null;
        }
    }


    public boolean ifServiceName(String serviceName){
        List<String> allserviceName = connectToCenter.getAllServiceName();
        for(String string: allserviceName){
            if(string.equalsIgnoreCase(serviceName)){
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        String a = "/mgateway/mweather/weather";
        System.out.println(a.replaceAll("/mgateway",""));
    }
}