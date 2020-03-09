package com.septemberhx.mgateway.core;

import com.septemberhx.common.bean.MRoutingBean;
import com.septemberhx.common.bean.gateway.MDepRequestCacheBean;
import com.septemberhx.common.service.dependency.BaseSvcDependency;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.PriorityBlockingQueue;

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

    private List<MDepRequestCacheBean> userRequestRecordList;

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

    public void recordUserDepRequest(String userId, BaseSvcDependency dependency) {
        this.userRequestRecordList.add(new MDepRequestCacheBean(dependency, userId, DateTime.now().getMillis()));
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

    private MGatewayInfo() {
        // for thread safety
        this.requestQueue = new PriorityBlockingQueue<>();
        this.userRoutingTable = new ConcurrentHashMap<>();
        this.userRoutingRecord = new ConcurrentHashMap<>();
        this.instRoutingTable = new ConcurrentHashMap<>();
        this.userRequestRecordList = new CopyOnWriteArrayList<>();
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
