package com.septemberhx.mgateway.core;

import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.bean.MRoutingBean;
import com.septemberhx.common.bean.gateway.MDepRequestCacheBean;
import com.septemberhx.common.service.dependency.BaseSvcDependency;
import lombok.Setter;
import org.joda.time.DateTime;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/7
 *
 * We use the instance ip to identify each instance instead of the id,
 *   so we don't need to search the instance list for each request.
 */
public class MGatewayInfo {
    private static MGatewayInfo instance;

    // hold the requests from clients
    // at the beginning of each time window
    private final PriorityBlockingQueue<MDepRequestCacheBean> requestQueue;

    private PriorityBlockingQueue<MDepRequestCacheBean> userRequestRecordQueue;

    private PriorityBlockingQueue<MDepRequestCacheBean> cannotSatisfiedRequestQueue;

    /*
     * Map [ cluster instance ip, cloud instance id ]
     */
    @Setter
    private Map<String, String> replaceMap;

    /*
     * Map {
     *   clientId : {
     *     userId: {
     *       dep: {
     *         routing info
     *       }
     *     }
     *   }
     * }
     */
    private Map<String, Map<String, Map<BaseSvcDependency, MRoutingBean>>> routingCache;

    private static final long MAX_RECORD_TIME_IN_MILLS = TimeUnit.HOURS.toMillis(1);

    public List<MDepRequestCacheBean> failedRequests() {
        return new ArrayList<>(cannotSatisfiedRequestQueue);
    }

    public void resetCache() {
        this.routingCache.clear();
    }

    public void retryFailedRequests() {
        synchronized (this.requestQueue) {
            while (!this.cannotSatisfiedRequestQueue.isEmpty()) {
                MDepRequestCacheBean cacheBean = this.cannotSatisfiedRequestQueue.poll();
                this.requestQueue.add(cacheBean);
            }
        }
    }

    public void recordUserDepRequest(MDepRequestCacheBean cacheBean) {
        this.userRequestRecordQueue.add(cacheBean);
        long lastTimeThreshold = DateTime.now().getMillis() - MAX_RECORD_TIME_IN_MILLS;

        // kick off the out of date cache
        MDepRequestCacheBean requestCacheBean = this.userRequestRecordQueue.poll();
        while (requestCacheBean != null && requestCacheBean.getTimestamp() < lastTimeThreshold) {
            requestCacheBean = this.userRequestRecordQueue.poll();
        }
        if (requestCacheBean != null) {
            this.userRequestRecordQueue.add(requestCacheBean);
        }
    }

    public List<MDepRequestCacheBean> getRequestBetweenTime(long startTimeInMills, long endTimeInMills) {
        MDepRequestCacheBean[] requestList = new MDepRequestCacheBean[this.userRequestRecordQueue.size()];
        requestList = this.userRequestRecordQueue.toArray(requestList);

        List<MDepRequestCacheBean> resultList = new ArrayList<>();
        for (MDepRequestCacheBean requestCacheBean : requestList) {
            if (requestCacheBean.getTimestamp() >= startTimeInMills
                    && requestCacheBean.getTimestamp() < endTimeInMills) {
                resultList.add(requestCacheBean);
            }
        }
        return resultList;
    }

    public MDepRequestCacheBean getNextRequestBlocking() throws InterruptedException {
        return this.requestQueue.take();
    }

    public void addRequestInQueue(String userId, BaseSvcDependency dependency, MResponse parameters, String nodeId, String simulationIdd, String callbackUrl) {
        this.requestQueue.offer(new MDepRequestCacheBean(dependency, userId, DateTime.now().getMillis(), parameters, nodeId, simulationIdd, callbackUrl));
    }


    public void recordCannotSatisfiedRequest(MDepRequestCacheBean requestCacheBean) {
        this.cannotSatisfiedRequestQueue.add(requestCacheBean);
    }

    private MGatewayInfo() {
        // for thread safety
        this.requestQueue = new PriorityBlockingQueue<>();
        this.userRequestRecordQueue = new PriorityBlockingQueue<>();
        this.cannotSatisfiedRequestQueue = new PriorityBlockingQueue<>();
        this.routingCache = new HashMap<>();
        this.replaceMap = new HashMap<>();
    }

    public static MGatewayInfo inst() {
        if (MGatewayInfo.instance == null) {
            synchronized (MGatewayInfo.class) {
                MGatewayInfo.instance = new MGatewayInfo();
            }
        }
        return MGatewayInfo.instance;
    }

    public Optional<MRoutingBean> getRouting(String instanceIp, BaseSvcDependency dependency, String userId) {
        if (this.routingCache.containsKey(instanceIp)
                && this.routingCache.get(instanceIp).containsKey(userId)
                && this.routingCache.get(instanceIp).get(userId).containsKey(dependency)) {
            return Optional.of(this.routingCache.get(instanceIp).get(userId).get(dependency));
        } else {
            return Optional.empty();
        }
    }

    public void cacheRouting(String instanceIp, BaseSvcDependency dep, String userId, MRoutingBean routingBean) {
        if (!this.routingCache.containsKey(instanceIp)) {
            this.routingCache.put(instanceIp, new HashMap<>());
        }

        if (!this.routingCache.get(instanceIp).containsKey(userId)) {
            this.routingCache.get(instanceIp).put(userId, new HashMap<>());
        }

        this.routingCache.get(instanceIp).get(userId).put(dep, routingBean);
    }

    public Optional<String> getReplacement(String instanceIp) {
        if (this.replaceMap.containsKey(instanceIp)) {
            return Optional.of(this.replaceMap.get(instanceIp));
        } else {
            return Optional.empty();
        }
    }

    public void reset() {
        this.requestQueue.clear();
        this.userRequestRecordQueue.clear();
        this.cannotSatisfiedRequestQueue.clear();
        this.routingCache.clear();
        this.replaceMap.clear();
    }
}
