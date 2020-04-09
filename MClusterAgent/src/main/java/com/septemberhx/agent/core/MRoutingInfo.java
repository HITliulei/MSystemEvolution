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

    private Map<String, Map<String, Map<BaseSvcDependency, MRoutingBean>>> routingTable;

    private Map<PureSvcDependency, String> pureRoutingMap;

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

    public Optional<MRoutingBean> findNewRoutingBean(BaseSvcDependency dep) {
        MService targetSvc = svcMap.get(this.pureRoutingMap.get(dep.getDep()));
        if (targetSvc != null) {
            Optional<MSvcInterface> apiOpt = targetSvc.getInterfaceByDep(dep.getDep());
            if (apiOpt.isPresent()) {
                for (MSvcInstance inst : svcInstanceMap.values()) {
                    if (inst.getServiceId().equals(this.pureRoutingMap.get(dep.getDep()))) {
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
        this.pureRoutingMap = new ConcurrentHashMap<>();
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
