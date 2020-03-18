package com.septemberhx.agent.core;

import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.bean.MRoutingBean;
import com.septemberhx.common.bean.gateway.MDepRequestCacheBean;
import com.septemberhx.common.service.dependency.BaseSvcDependency;
import com.septemberhx.common.service.dependency.PureSvcDependency;
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
public class MRoutingInfo {
    private static MRoutingInfo instance;

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
    private Map<PureSvcDependency, Map<MRoutingBean, Integer>> userRoutingTable;

    /*
     * Map[
     *   dep,
     *   Map[
     *     routing info,
     *     Set<User Id>  # stands for user set that is redirected to this routing info by this gateway
     *   ]
     * ]
     */
    private Map<PureSvcDependency, Map<MRoutingBean, Set<String>>> userRoutingRecord;

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

    public Optional<MRoutingBean> getRoutingForInst(String fromIpAddr, BaseSvcDependency dependency) {
        if (this.instRoutingTable.containsKey(fromIpAddr)) {
            return Optional.of(this.instRoutingTable.get(fromIpAddr).get(dependency));
        }
        return Optional.empty();
    }

    public Optional<MRoutingBean> getRoutingFromRecordForUser(String userId, BaseSvcDependency dependency) {
        // check if new demand
        if (!this.userRoutingTable.containsKey(dependency.getDep())) {
            return Optional.empty();
        }

        // check if assigned before
        if (userRoutingRecord.containsKey(dependency.getDep())) {
            for (MRoutingBean routingBean : userRoutingRecord.get(dependency.getDep()).keySet()) {
                if (userRoutingRecord.get(dependency.getDep()).get(routingBean).contains(userId)) {
                    return Optional.of(routingBean);
                }
            }
        }

        return Optional.empty();
    }

    public Optional<MRoutingBean> getRoutingFromTableForUser(BaseSvcDependency dependency) {
        if (this.userRoutingTable.containsKey(dependency.getDep())) {
            // check if has available plot for the new demand
            Map<MRoutingBean, Integer> depRoutingTable = userRoutingTable.get(dependency.getDep());
            for (MRoutingBean routingBean : depRoutingTable.keySet()) {
                if (depRoutingTable.get(routingBean) > this.userRoutingRecord.get(dependency.getDep()).get(routingBean).size()) {
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
        if (!this.userRoutingRecord.containsKey(dependency.getDep())) {
            this.userRoutingRecord.put(dependency.getDep(), new ConcurrentHashMap<>());
        }

        if (!this.userRoutingRecord.get(dependency.getDep()).containsKey(routingBean)) {
            this.userRoutingRecord.get(dependency.getDep()).put(routingBean, new CopyOnWriteArraySet());
        }

        this.userRoutingRecord.get(dependency.getDep()).get(routingBean).add(userId);
    }


    private MRoutingInfo() {
        // for thread safety
        this.userRoutingTable = new ConcurrentHashMap<>();
        this.userRoutingRecord = new ConcurrentHashMap<>();
        this.instRoutingTable = new ConcurrentHashMap<>();
    }

    public static MRoutingInfo inst() {
        if (MRoutingInfo.instance == null) {
            synchronized (MRoutingInfo.class) {
                MRoutingInfo.instance = new MRoutingInfo();
            }
        }
        return MRoutingInfo.instance;
    }
}
