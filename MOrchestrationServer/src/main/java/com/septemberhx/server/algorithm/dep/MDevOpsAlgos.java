package com.septemberhx.server.algorithm.dep;

import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.MSvcInstance;
import com.septemberhx.common.service.dependency.BaseSvcDependency;
import com.septemberhx.common.service.dependency.PureSvcDependency;
import com.septemberhx.server.job.MBaseJob;
import com.septemberhx.server.job.MDeleteJob;
import com.septemberhx.server.job.MDeployJob;
import com.septemberhx.server.model.MSystemModel;
import com.septemberhx.server.utils.MIDUtils;

import java.util.*;

public class MDevOpsAlgos {

    // Please attention !!!!
    // RoutingMap will be modified !!!!!!!!!!!
    public static List<MService> getMiniServiceSet(MService wantedSvc, List<MSvcInstance> existInsts,
                                                   Map<PureSvcDependency, MService> routingMap, MSystemModel currModel) {
        List<MService> svcList = new ArrayList<>();
        for (BaseSvcDependency dep : wantedSvc.getAllDep()) {
            boolean ifWorked = false;
            for (MSvcInstance inst : existInsts) {
                Optional<MService> svcOpt = currModel.getServiceManager().getById(inst.getServiceId());
                if (svcOpt.isPresent() && MappingSvcAlgos.checkIfSvcMeetDep(svcOpt.get(), dep.getDep())) {
                    // todo: record the routing rules
                    routingMap.put(dep.getDep(), svcOpt.get());

                    ifWorked = true;
                    break;
                }
            }

            if (!ifWorked) {
                List<MService> fitSvcList = new ArrayList<>();

                // use included svc first
                boolean existSvcFit = false;
                for (MService service : svcList) {
                    if (MappingSvcAlgos.checkIfSvcMeetDep(service, dep.getDep())) {
                        existSvcFit = true;
                        break;
                    }
                }
                if (existSvcFit) {
                    continue;
                }

                // or search all services and find the best one
                for (MService service : currModel.getServiceManager().getAllValues()) {
                    if (MappingSvcAlgos.checkIfSvcMeetDep(service, dep.getDep())) {
                        fitSvcList.add(service);
                    }
                }
                fitSvcList.sort(Comparator.comparingLong(service -> service.getResource().getCpu()));
                svcList.add(fitSvcList.get(0));
            }
        }
        return svcList;
    }

    public static List<MBaseJob> deleteInst(String instId, List<MSvcInstance> existInsts, Map<PureSvcDependency, MService> routingMap, boolean ifDep, MSystemModel currModel) {
        Map<String, MSvcInstance> existInstMap = new HashMap<>();
        for (MSvcInstance svcInst : existInsts) {
            existInstMap.put(svcInst.getId(), svcInst);
        }

        List<MBaseJob> jobList = new ArrayList<>();
        _deleteInst(instId, existInstMap, routingMap, currModel, jobList, ifDep);
        return jobList;
    }

    private static void _deleteInst(String instId, Map<String, MSvcInstance> existInsts, Map<PureSvcDependency, MService> routingMap, MSystemModel currModel, List<MBaseJob> jobList, boolean ifDep) {
        MSvcInstance targetInst = existInsts.get(instId);
        MDeleteJob deleteJob = new MDeleteJob(targetInst.getId(), targetInst.getServiceId(), targetInst.getNodeId());
        jobList.add(deleteJob);
        existInsts.remove(targetInst.getId());

        if (!ifDep) {
            return;
        }

        Optional<MService> svcOpt = currModel.getServiceManager().getById(targetInst.getServiceId());
        if (svcOpt.isPresent()) {
            for (BaseSvcDependency dependency : svcOpt.get().getAllDep()) {
                if (routingMap.containsKey(dependency.getDep())) {
                    MService svc = routingMap.get(dependency.getDep());
                    if (!checkIfInstUseSvc(existInsts, svc, routingMap, currModel)) {
                        // remove all the instances of service svc if svc is not used by other services
                        Set<String> deleteInstIdSet = new HashSet<>();
                        for (MSvcInstance instance : existInsts.values()) {
                            if (instance.getServiceId().equals(svc.getId())) {
                                deleteInstIdSet.add(instance.getId());
                            }
                        }
                        for (String currInstId : deleteInstIdSet) {
                            _deleteInst(currInstId, existInsts, routingMap, currModel, jobList, ifDep);
                        }
                    }
                }
            }
        }
    }

    public static boolean checkIfInstUseSvc(Map<String, MSvcInstance> existInstMap, MService svc, Map<PureSvcDependency, MService> routingMap, MSystemModel currModel) {
        for (MSvcInstance instance : existInstMap.values()) {
            Optional<MService> svcOpt = currModel.getServiceManager().getById(instance.getServiceId());
            if (svcOpt.isPresent()) {
                for (BaseSvcDependency dependency : svcOpt.get().getAllDep()) {
                    if (routingMap.containsKey(dependency.getDep()) && routingMap.get(dependency.getDep()).getId().equals(svc.getId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static List<MBaseJob> deployInst(MService service, String nodeId, boolean ifDep, List<MSvcInstance> existInsts,
                                            Map<PureSvcDependency, MService> routingMap, MSystemModel currModel) {
        List<MBaseJob> jobList = new ArrayList<>();
        if (!ifDep) {
            MDeployJob deployJob = new MDeployJob(nodeId, service.getServiceName(),
                    MIDUtils.uniqueInstanceId(service.getServiceName(), service.getServiceVersion().toString()), service.getImageUrl());
            jobList.add(deployJob);
        } else {
            // 注意，这里 routingMap 会被修改！！！
            // existinsts 也会被修改 !!!
            List<MService> svcList = getMiniServiceSet(service, existInsts, routingMap, currModel);
            for (MService svc : svcList) {
                MDeployJob deployJob = new MDeployJob(nodeId, svc.getServiceName(),
                        MIDUtils.uniqueInstanceId(svc.getServiceName(), svc.getServiceVersion().toString()), svc.getImageUrl());
                jobList.add(deployJob);

                existInsts.add(new MSvcInstance(
                        new HashMap<>(),
                        "",
                        deployJob.getNodeId(),
                        null,
                        0,
                        deployJob.getUniqueId(),
                        new HashSet<>(),
                        svc.getServiceName(),
                        svc.getId(),
                        "",
                        svc.getServiceVersion().toString()
                ));
            }
        }
        return jobList;
    }

    public static List<MBaseJob> upgradeInst(MSvcInstance targetInst, List<MSvcInstance> existInsts, MService targetSvc, boolean ifDep, Map<PureSvcDependency, MService> routingMap, MSystemModel currModel) {
        // 注意, routingMap 绘被修改
        List<MBaseJob> jobList = deployInst(targetSvc, targetInst.getNodeId(), ifDep, existInsts, routingMap, currModel);
        List<MBaseJob> deleteJobList = deleteInst(targetInst.getId(), existInsts, routingMap, ifDep, currModel);
        jobList.addAll(deleteJobList);
        return jobList;
    }

    public static List<MBaseJob> upgradeSvc(List<MSvcInstance> existInsts, MService oldSvc, MService newSvc, boolean ifDep, Map<PureSvcDependency, MService> routingMap, MSystemModel currModel) {
        List<MSvcInstance> deleteList = new ArrayList<>();
        List<MBaseJob> jobList = new ArrayList<>();

        for (MSvcInstance svcInstance : existInsts) {
            if (svcInstance.getServiceId().equals(oldSvc.getId())) {
                deleteList.add(svcInstance);
                jobList.addAll(deployInst(newSvc, svcInstance.getNodeId(), ifDep, existInsts, routingMap, currModel));
            }
        }

        for (MSvcInstance svcInstance : deleteList) {
            jobList.addAll(deleteInst(svcInstance.getId(), existInsts, routingMap, ifDep, currModel));
        }
        return jobList;
    }

    public static void changeDep(MService svc, BaseSvcDependency svcDependency, PureSvcDependency newDep, boolean ifDep) {

    }
}
