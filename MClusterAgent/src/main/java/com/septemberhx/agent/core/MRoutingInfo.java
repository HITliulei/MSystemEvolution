package com.septemberhx.agent.core;

import com.septemberhx.common.bean.MRoutingBean;
import com.septemberhx.common.exception.NonexistenServiceException;
import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.MSvcInstance;
import com.septemberhx.common.service.MSvcInterface;
import com.septemberhx.common.service.dependency.BaseSvcDependency;
import com.septemberhx.common.service.dependency.PureSvcDependency;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/7
 */
public class MRoutingInfo {
    private static MRoutingInfo instance;

    /*
     * Routing guideline. It tells the system what service should be used to satisfy each kind of dep
     * Map[ nodeId, [dep, service id ]]
     */
    private Map<String, Map<PureSvcDependency, String>> pureInstRoutingMap;
    private Map<String, Map<PureSvcDependency, String>> pureUserRoutingMap;

    @Setter
    private Map<String, MService> svcMap;

    @Setter
    private Map<String, MSvcInstance> svcInstanceMap;

    /* used user number for stable SLA of each instance each interface
     *
     * For one user that calls any API of one instance,
     *   the plot is occupied by 1 * n, where n is the coefficient in dep;
     * For instance that calls any API of one instance,
     *   the plot is occupied by m * n, where m is the used plots in of the invoking instance;
     *
     * Map[instanceId, Map[interfaceId, used number]]
     */
    private Map<String, Map<String, Map<String, Integer>>> usedPlot;

    /*
     * This table is used to make sure each time window the same dep of the
     *   same user will be directed to the same inst.
     */
    private Map<String, Map<String, Map<BaseSvcDependency, MRoutingBean>>> routingTable;

    public void resetRoutingMap(Map<String, Map<PureSvcDependency, String>> rMap, Map<String, Map<PureSvcDependency, String>> uMap, Map<String, MService> svcMap, Map<String, MSvcInstance> svcInstanceMap) {
        this.usedPlot.clear();
        this.routingTable.clear();
        this.pureInstRoutingMap = rMap;
        this.pureUserRoutingMap = uMap;
        this.svcMap = svcMap;
        this.svcInstanceMap = svcInstanceMap;
    }

    public Optional<MRoutingBean> getRoutingFromRecord(String clientId, String userId, BaseSvcDependency dependency) {
        if (this.routingTable.containsKey(clientId) && this.routingTable.containsKey(userId) && this.routingTable.containsKey(dependency)) {
            return Optional.of(this.routingTable.get(clientId).get(userId).get(dependency));
        }

        return Optional.empty();
    }

    /*
     * Occupy one plot for given routing info
     */
    public void recordRouting(String clientId, String callerPatternUrl, String userId, BaseSvcDependency dependency, MRoutingBean routingBean) {
        if (!this.routingTable.containsKey(clientId)) {
            this.routingTable.put(clientId, new HashMap<>());
        }

        if (!this.routingTable.get(clientId).containsKey(userId)) {
            this.routingTable.get(clientId).put(userId, new HashMap<>());
        }
        this.routingTable.get(clientId).get(userId).put(dependency, routingBean);

        MSvcInstance targetInst = null;
        for (MSvcInstance svcInstance : svcInstanceMap.values()) {
            if (svcInstance.getIp().equals(routingBean.getIpAddr())) {
                targetInst = svcInstance;
                break;
            }
        }
        if (targetInst != null) {
            MService svc = svcMap.get(targetInst.getServiceId());
            Optional<MSvcInterface> apiOpt = svc.getInterfaceByPatternUrl(routingBean.getPatternUrl());
            int usedPlot = 1;
            if (apiOpt.isPresent()) {
                if (svcInstanceMap.containsKey(clientId)) {
                    MService callerSvc = svcMap.get(svcInstanceMap.get(clientId).getServiceId());
                    Optional<MSvcInterface> callerApiOpt = callerSvc.getInterfaceByPatternUrl(callerPatternUrl);
                    if (callerApiOpt.isPresent()) {
                        usedPlot *= callerApiOpt.get().getInvokeCountMap().getOrDefault(dependency.hashCode(), 1);
                    }
                }
            }
            if (!this.usedPlot.containsKey(clientId)) {
                this.usedPlot.put(clientId, new HashMap<>());
            }
            if (!this.usedPlot.get(clientId).containsKey(routingBean.getPatternUrl())) {
                this.usedPlot.get(clientId).put(routingBean.getPatternUrl(), new HashMap<>());
            }
            this.usedPlot.get(clientId).get(routingBean.getPatternUrl())
                    .put(userId, usedPlot + this.usedPlot.get(clientId).get(routingBean.getPatternUrl()).getOrDefault(userId, 0));
        }
    }

    public Optional<MRoutingBean> findNewRoutingBean(BaseSvcDependency dep, String nodeId, boolean ifUser) {
        Map<String, Map<PureSvcDependency, String>> routingMap = this.pureInstRoutingMap;
        if (ifUser) {
            routingMap = this.pureUserRoutingMap;
        }

        if (routingMap.containsKey(nodeId) && routingMap.get(nodeId).containsKey(dep.getDep())) {
            MService targetSvc = svcMap.get(routingMap.get(nodeId).get(dep.getDep()));
            if (targetSvc != null) {
                Optional<MSvcInterface> apiOpt = targetSvc.getInterfaceByDep(dep.getDep());
                if (apiOpt.isPresent()) {
                    for (MSvcInstance inst : svcInstanceMap.values()) {
                        if (inst.getServiceId().equals(routingMap.get(nodeId).get(dep.getDep()))) {
                            if (checkInstHasAvailablePlot(inst.getId(), apiOpt.get().getInvokeCountMap().get(dep.hashCode()))) {
                                return Optional.of(new MRoutingBean(
                                        inst.getIp(),
                                        inst.getPort(),
                                        apiOpt.get().getPatternUrl()
                                ));
                            }
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

    public boolean checkInstHasAvailablePlot(String instanceId, int plotNum) {
        MSvcInstance svcInstance = this.svcInstanceMap.get(instanceId);
        if (svcInstance != null) {
            MService service = this.svcMap.get(svcInstance.getServiceId());
            if (service != null) {
                if (this.getUsedPlotNum(instanceId) + plotNum >= service.getMaxPlotNum()) {
                    return false;
                }
                return true;
            } else {
                throw new NonexistenServiceException(svcInstance.getServiceId());
            }
        }
        return false;
    }

    public int getUsedPlotNum(String instanceId) {
        int totalNum = 0;
        if (this.usedPlot.containsKey(instanceId)) {
            for (String apiUrl : this.usedPlot.get(instanceId).keySet()) {
                for (String userId : this.usedPlot.get(instanceId).get(apiUrl).keySet()) {
                    totalNum += this.usedPlot.get(instanceId).get(apiUrl).get(userId);
                }
            }
        }
        return totalNum;
    }

    private MRoutingInfo() {
        // for thread safety
        this.routingTable = new ConcurrentHashMap();
        this.pureInstRoutingMap = new ConcurrentHashMap<>();
        this.pureUserRoutingMap = new ConcurrentHashMap<>();
        this.usedPlot = new ConcurrentHashMap<>();
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
