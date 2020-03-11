package com.septemberhx.mgateway.core;

import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.bean.MRoutingBean;
import com.septemberhx.common.bean.gateway.MDepRequestCacheBean;
import com.septemberhx.common.service.dependency.BaseSvcDependency;
import org.joda.time.DateTime;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/7
 */
public class MGatewayInfo {
    private static MGatewayInfo instance;

    // hold the requests from clients
    // at the beginning of each time window
    private PriorityBlockingQueue<MDepRequestCacheBean> requestQueue;

    private PriorityBlockingQueue<MDepRequestCacheBean> userRequestRecordQueue;

    private PriorityBlockingQueue<MDepRequestCacheBean> cannotSatisfiedRequestQueue;

    private static final long MAX_RECORD_TIME_IN_MILLS = TimeUnit.HOURS.toMillis(1);

    /*
     * Map[
     *   dep,
     *   Map[
     *     routing info,
     *     max user count  # stands for how many users can by redirected to this routing info by this gateway
     *   ]
     * ]
     */
    private Map<BaseSvcDependency, Map<MRoutingBean, Integer>> userRoutingTable;

    /*
     * Map[
     *   dep,
     *   Map[
     *     routing info,
     *     Set<User Id>  # stands for user set that is redirected to this routing info by this gateway
     *   ]
     * ]
     */
    private Map<BaseSvcDependency, Map<MRoutingBean, Set<String>>> userRoutingRecord;

    /*
     * Map[
     *   instance id,
     *   Map[
     *     dep,
     *     routing info
     *   ]
     * ]
     * Specific for routing between services, will not change in one time window
     */
    private Map<String, Map<BaseSvcDependency, MRoutingBean>> instRoutingTable;

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

    public void addRequestInQueue(String userId, BaseSvcDependency dependency, MResponse parameters) {
        this.requestQueue.offer(new MDepRequestCacheBean(dependency, userId, DateTime.now().getMillis(), parameters));
    }

    public Optional<MRoutingBean> getRoutingForInst(String fromIpAddr, BaseSvcDependency dependency) {
        if (this.instRoutingTable.containsKey(fromIpAddr)) {
            return Optional.of(this.instRoutingTable.get(fromIpAddr).get(dependency));
        }
        return Optional.empty();
    }

    public Optional<MRoutingBean> getRoutingFromRecordForUser(String userId, BaseSvcDependency dependency) {
        // check if new demand
        if (!this.userRoutingTable.containsKey(dependency)) {
            return Optional.empty();
        }

        // check if assigned before
        if (userRoutingRecord.containsKey(dependency)) {
            for (MRoutingBean routingBean : userRoutingRecord.get(dependency).keySet()) {
                if (userRoutingRecord.get(dependency).get(routingBean).contains(userId)) {
                    return Optional.of(routingBean);
                }
            }
        }

        return Optional.empty();
    }

    public Optional<MRoutingBean> getRoutingFromTableForUser(BaseSvcDependency dependency) {
        if (this.userRoutingTable.containsKey(dependency)) {
            // check if has available plot for the new demand
            Map<MRoutingBean, Integer> depRoutingTable = userRoutingTable.get(dependency);
            for (MRoutingBean routingBean : depRoutingTable.keySet()) {
                if (depRoutingTable.get(routingBean) > this.userRoutingRecord.get(dependency).get(routingBean).size()) {
                    return Optional.of(routingBean);
                }
            }
        }
        return Optional.empty();
    }

    /*
     * Occupy one plot for given routing info
     */
    public void recordUserRouting(String userId, BaseSvcDependency dependency, MRoutingBean routingBean) {
        if (!this.userRoutingRecord.containsKey(dependency)) {
            this.userRoutingRecord.put(dependency, new ConcurrentHashMap<>());
        }

        if (!this.userRoutingRecord.get(dependency).containsKey(routingBean)) {
            this.userRoutingRecord.get(dependency).put(routingBean, new CopyOnWriteArraySet());
        }

        this.userRoutingRecord.get(dependency).get(routingBean).add(userId);
    }

    public void recordCannotSatisfiedRequest(MDepRequestCacheBean requestCacheBean) {
        this.cannotSatisfiedRequestQueue.add(requestCacheBean);
    }

    private MGatewayInfo() {
        // for thread safety
        this.requestQueue = new PriorityBlockingQueue<>();
        this.userRoutingTable = new ConcurrentHashMap<>();
        this.userRoutingRecord = new ConcurrentHashMap<>();
        this.instRoutingTable = new ConcurrentHashMap<>();
        this.userRequestRecordQueue = new PriorityBlockingQueue<>();
        this.cannotSatisfiedRequestQueue = new PriorityBlockingQueue<>();
    }

    public static MGatewayInfo inst() {
        if (MGatewayInfo.instance == null) {
            synchronized (MGatewayInfo.class) {
                MGatewayInfo.instance = new MGatewayInfo();
            }
        }
        return MGatewayInfo.instance;
    }
}
