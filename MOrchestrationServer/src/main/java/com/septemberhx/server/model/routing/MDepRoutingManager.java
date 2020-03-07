package com.septemberhx.server.model.routing;


import com.septemberhx.common.exception.NonexistenServiceException;
import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.MSvcInterface;
import com.septemberhx.common.service.dependency.BaseSvcDependency;
import com.septemberhx.server.model.MSvcManager;
import org.javatuples.Pair;

import java.util.*;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/7
 */
public class MDepRoutingManager {

    /*
     * Routing table for each users/instances with dependency
     *
     * Map[
     *   userId/instanceId: Map[
     *     dependency: (instanceId, interfaceId)
     *   ]
     * ]
     */
    private Map<String, Map<BaseSvcDependency, Pair<String, String>>> routingTable;

    /*
     * used user number for stable SLA of each instance each interface
     *
     * For one user that calls any API of one instance,
     *   the plot is occupied by 1 * n, where n is the coefficient in dep;
     * For instance that calls any API of one instance,
     *   the plot is occupied by m * n, where m is the used plots in of the invoking instance;
     *
     * Map[instanceId, Map[interfaceId, used number]]
     */
    private Map<String, Map<String, Integer>> usedPlot;

    public void addNewInstance(String instanceId, String serviceId, MSvcManager currSvcManager) {
        Optional<MService> serviceOpt = currSvcManager.getById(serviceId);
        if (serviceOpt.isPresent()) {
            this.usedPlot.put(instanceId, new HashMap<>());
            this.routingTable.put(instanceId, new HashMap<>());
        } else {
            throw new NonexistenServiceException(serviceId);
        }
    }

    /*
     * Delete one instance complete in the routing table.
     *
     * The dependency that invokes the given instance is returned.
     */
    private Map<String, List<BaseSvcDependency>> deleteInstance(
            String instanceId, String serviceId, MSvcManager currSvcManager) {
        Optional<MService> serviceOpt = currSvcManager.getById(serviceId);
        if (serviceOpt.isPresent()) {

            // Get the instances that is invoked by this instance,
            //   and restore their available plot number.
            Map<BaseSvcDependency, Pair<String, String>> invokedInstDeps = this.routingTable.get(instanceId);
            for (BaseSvcDependency svcDependency : invokedInstDeps.keySet()) {
                Pair<String, String> pair = invokedInstDeps.get(svcDependency);

                // the interface that calls pair.getValue1()
                for (MSvcInterface svcInterface : this.getInterfaceIdsCalledDep(svcDependency, serviceOpt.get())) {
                    int coe = svcInterface.getInvokeCountMap().get(svcDependency);
                    int plotCount = this.usedPlot.get(instanceId).get(svcInterface.getId());
                    int releaseCount = coe * plotCount;
                    this.releasePlotNum(pair.getValue0(), pair.getValue1(), releaseCount, currSvcManager);
                }
            }

            // Delete its used plot number
            this.usedPlot.remove(instanceId);

            // Return the instance deps that invoke this instance
            return this.getInvokingMapping(instanceId);
        } else {
            throw new NonexistenServiceException(serviceId);
        }
    }

    /*
     * Use the information that extracted from source code to figure out which instance depends on given dependency
     */
    private List<MSvcInterface> getInterfaceIdsCalledDep(BaseSvcDependency dependency, MService service) {
        return service.getInterfacesContainDep(dependency);
    }

    private void releasePlotNum(String instanceId, String interfaceId, int releaseNum, MSvcManager svcManager) {
        // release itself
        this.usedPlot.get(instanceId).put(
                interfaceId,
                this.usedPlot.get(instanceId).get(interfaceId) - releaseNum
        );

        // release the invoked interface in the given interface
        Optional<MSvcInterface> interfaceOpt = svcManager.getInterfaceById(interfaceId);
        if (interfaceOpt.isPresent()) {
            for (BaseSvcDependency svcDependency : interfaceOpt.get().getInvokeCountMap().keySet()) {
                Pair<String, String> instIdInterfaceIdPair = this.routingTable.get(instanceId).get(svcDependency);
                int coe = interfaceOpt.get().getInvokeCountMap().get(svcDependency);
                this.releasePlotNum(
                        instIdInterfaceIdPair.getValue0(),
                        instIdInterfaceIdPair.getValue1(),
                        coe * releaseNum, svcManager
                );
            }
        }
    }

    /*
     * Get the list of instance id and dep map that **invokes** the given instance
     *
     * Since we also put the user dependency into the same routing table, the return value also contains the user part.
     */
    private Map<String, List<BaseSvcDependency>> getInvokingMapping(String instanceId) {
        Map<String, List<BaseSvcDependency>> resultMap = new HashMap<>();
        for (String tmpInstId : routingTable.keySet()) {
            for (BaseSvcDependency svcDependency : routingTable.get(tmpInstId).keySet()) {
                String invokedInstId = routingTable.get(tmpInstId).get(svcDependency).getValue0();
                if (instanceId.equals(invokedInstId)) {
                    if (!resultMap.containsKey(tmpInstId)) {
                        resultMap.put(tmpInstId, new ArrayList<>());
                    }
                    resultMap.get(tmpInstId).add(svcDependency);
                }
            }
        }
        return resultMap;
    }

    public MDepRoutingManager() {
        this.routingTable = new HashMap<>();
        this.usedPlot = new HashMap<>();
    }
}
