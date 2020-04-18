package com.septemberhx.server.algorithm.dep;

import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.MSla;
import com.septemberhx.common.service.MSvcInterface;
import com.septemberhx.common.service.MSvcVersion;
import com.septemberhx.common.service.dependency.*;
import com.septemberhx.common.utils.CommonUtils;
import com.septemberhx.server.model.MSvcManager;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/15
 */
public class MappingSvcAlgos {

    public static MSvcManager svcManager;
    private static Logger logger = LogManager.getLogger(MappingSvcAlgos.class);

    @Getter
    @Setter
    static class TempSvc {
        private String serviceName;
        private Set<MSvcVersion> versionSet;

        public TempSvc(String serviceName, Set<MSvcVersion> versionSet) {
            this.serviceName = serviceName;
            this.versionSet = versionSet;
        }
    }

    /*
     * The same user ask two apis of one services is counting as two user
     */
    public static Map<MService, Integer> calcSvcUserCount(
            List<MService> demandSvcList, Map<PureSvcDependency, MService> svcDepMap,
            Map<PureSvcDependency, MService> userDepMap, Map<PureSvcDependency, Integer> userDepSet) {
        Map<MService, Integer> resultMap = new HashMap<>();
        Set<MService> allSvcSet = new HashSet<>(demandSvcList);
        allSvcSet.addAll(svcDepMap.values());
        for (MService svc : allSvcSet) {
            resultMap.put(svc, 0);
        }

        for (PureSvcDependency svcDependency : userDepMap.keySet()) {
            MService targetSvc = userDepMap.get(svcDependency);
            Optional<MSvcInterface> apiOpt = targetSvc.getInterfaceByDep(svcDependency);
            apiOpt.ifPresent(svcInterface -> _calcSvcUserCount(targetSvc, svcInterface, userDepSet.get(svcDependency), svcDepMap, resultMap));
        }
        return resultMap;
    }

    public static void _calcSvcUserCount(MService calledSvc, MSvcInterface calledApi, int calledCount,
                                         Map<PureSvcDependency, MService> svcDepMap, Map<MService, Integer> countMap) {
        countMap.put(calledSvc, countMap.getOrDefault(calledSvc, 0) + calledCount);
        for (Integer svcDepHashCode : calledApi.getInvokeCountMap().keySet()) {
            Optional<BaseSvcDependency> depOpt = calledSvc.getDepByHashCode(svcDepHashCode);
            if (depOpt.isPresent()) {
                MService targetSvc = svcDepMap.get(depOpt.get().getDep());
                Optional<MSvcInterface> apiOpt = targetSvc.getInterfaceByDep(depOpt.get().getDep());
                apiOpt.ifPresent(svcInterface -> _calcSvcUserCount(
                        targetSvc, svcInterface, calledCount * calledApi.getInvokeCountMap().get(svcDepHashCode), svcDepMap, countMap));
            }
        }
    }

    public static Map<PureSvcDependency, MService> buildSvcTree(Set<MService> demandSvcSet) {
        return _buildSvcTree(demandSvcSet, demandSvcSet);
    }

    public static Map<PureSvcDependency, MService> _buildSvcTree(Set<MService> solvedSvcSet, Set<MService> unsolvedSvcSet) {
        Set<BaseSvcDependency> depSet = new HashSet<>();
        for (MService svc : unsolvedSvcSet) {
            depSet.addAll(svc.allDepList());
        }

        List<MService> svcList = svcManager.getAllValues();
        Map<MService, Set<BaseSvcDependency>> metMap = new HashMap<>();
        for (MService svc : svcList) {
            Set<BaseSvcDependency> tmpDepSet = new HashSet<>();
            for (BaseSvcDependency svcDependency : depSet) {
                if (checkIfSvcMeetDep(svc, svcDependency.getDep())) {
                    tmpDepSet.add(svcDependency);
                }
            }
            if (!tmpDepSet.isEmpty()) {
                metMap.put(svc, tmpDepSet);
            }
        }

        Map<PureSvcDependency, MService> mapResult = new HashMap<>();
        Set<MService> tmpSolvedSvcSet = new HashSet<>(solvedSvcSet);
        tmpSolvedSvcSet.addAll(unsolvedSvcSet);
        while (!depSet.isEmpty()) {
            List<MService> targetSvcList = new ArrayList<>(metMap.keySet());
            targetSvcList.sort((o1, o2) -> {
                if (tmpSolvedSvcSet.contains(o1) && !tmpSolvedSvcSet.contains(o2)) {
                    return -1;
                } else if (!tmpSolvedSvcSet.contains(o1) && tmpSolvedSvcSet.contains(o2)) {
                    return 1;
                } else {
                    if (metMap.get(o1).size() != metMap.get(o2).size()) {
                        return -Integer.compare(metMap.get(o1).size(), metMap.get(o2).size());
                    } else {
                        return -o1.getServiceVersion().compareTo(o2.getServiceVersion());
                    }
                }
            });
            if (targetSvcList.isEmpty()) {
                logger.error("Failed to satisfy for some demands");
                break;
            }
            MService targetSvc = targetSvcList.get(0);

            for (BaseSvcDependency svcDependency : metMap.get(targetSvc)) {
                mapResult.put(svcDependency.getDep(), targetSvc);
                depSet.remove(svcDependency);
            }
            for (Set<BaseSvcDependency> tmpSet : metMap.values()) {
                tmpSet.removeAll(metMap.get(targetSvc));
            }
            metMap.remove(targetSvc);
        }

        Set<MService> newUnsolvedSvcSet = new HashSet<>(mapResult.values());
        newUnsolvedSvcSet.removeAll(tmpSolvedSvcSet);

        if (!newUnsolvedSvcSet.isEmpty()) {
            Map<PureSvcDependency, MService> recursionResult = _buildSvcTree(tmpSolvedSvcSet, newUnsolvedSvcSet);
            for (PureSvcDependency svcDependency : recursionResult.keySet()) {
                mapResult.put(svcDependency, recursionResult.get(svcDependency));
            }
        }
        return mapResult;
    }

    public static Map<PureSvcDependency, MService> mappingFuncDepList(
            Map<PureSvcDependency, Integer> depCount, Set<PureSvcDependency> depSetRaw) {
        Set<PureSvcDependency> depSet = new HashSet<>(depSetRaw);

        List<MService> svcList = svcManager.getAllValues();
        Map<MService, Set<PureSvcDependency>> metMap = new HashMap<>();
        for (MService svc : svcList) {
            Set<PureSvcDependency> meetDep = new HashSet<>();
            for (PureSvcDependency dep : depSet) {
                if (checkIfSvcMeetDep(svc, dep)) {
                    meetDep.add(dep);
                }
            }
            metMap.put(svc, meetDep);
        }

        Map<PureSvcDependency, MService> mapResult = new HashMap<>();
        while (!depSet.isEmpty()) {
            List<MService> targetSvcList = new ArrayList<>(metMap.keySet());
            targetSvcList.sort(Comparator.comparingInt(v -> metMap.get(v).stream().mapToInt(depCount::get).sum()));
            if (targetSvcList.size() > 0) {
                MService targetSvc = targetSvcList.get(targetSvcList.size() - 1);

                for (PureSvcDependency svcDependency : metMap.get(targetSvc)) {
                    mapResult.put(svcDependency, targetSvc);
                    depSet.remove(svcDependency);
                }
                for (Set<PureSvcDependency> tmpSet : metMap.values()) {
                    tmpSet.removeAll(metMap.get(targetSvc));
                }
                metMap.remove(targetSvc);
            } else {
                logger.warn("All the services can not satisfy the left dependencies");
                logger.warn(String.format("There are %d dependencies can not be met", depSet.size()));
                break;
            }
        }
        return mapResult;
    }

    public static boolean checkIfSvcMeetDep(MService svc, PureSvcDependency svcDependency) {
        BaseSvcDependency dep = BaseSvcDependency.tranPure(svcDependency);
        if (dep instanceof SvcFuncDependency) {
            for (MSla sla : svcDependency.getSlaSet()) {
                if (svc.ifSatisfied(svcDependency.getFunc(), sla)) {
                    return true;
                }
            }
        } else if (dep instanceof SvcSlaDependency) {
            if (svc.getServiceName().toLowerCase().equals(svcDependency.getServiceName().toLowerCase())) {
                Optional<MSvcInterface> apiOpt = svc.getInterfaceByPatternUrl(svcDependency.getPatternUrl());
                if (apiOpt.isPresent() && svcDependency.getSlaSet().contains(apiOpt.get().getFuncDescription().getSla())) {
                    return true;
                }
            }
        } else if (dep instanceof SvcVerDependency) {
            if (svc.getServiceName().toLowerCase().equals(svcDependency.getServiceName().toLowerCase())
                    && svcDependency.getVersionSet().contains(svc.getServiceVersion())) {
                return true;
            }
        }
        return false;
    }

    // ------> Abandoned plan below <------
    @Deprecated
    public static void mappingVerDepList(Map<PureSvcDependency, Integer> depCount, List<PureSvcDependency> depList) {
        Map<PureSvcDependency, TempSvc> tempSvcMap = new HashMap<>();
        List<PureSvcDependency> depListCopy = new ArrayList<>(depList);
        Map<TempSvc, Integer> tempSvcCount = new HashMap<>();

        while (!depListCopy.isEmpty()) {
            depListCopy.sort(Comparator.comparingInt(depCount::get));
            PureSvcDependency currDep = depListCopy.get(depListCopy.size() - 1);
            Set<MSvcVersion> currVerSet = currDep.getVersionSet();
            Set<PureSvcDependency> infectedDepSet = new HashSet<>();
            infectedDepSet.add(currDep);
            for (int j = depListCopy.size() - 2; j >= 0; --j) {
                if (!currDep.getServiceName().equals(depListCopy.get(j).getServiceName())) {
                    continue;
                }

                Set<MSvcVersion> verSet = CommonUtils.getSetIntersection(currVerSet, depListCopy.get(j).getVersionSet());
                if (!verSet.isEmpty()) {
                    infectedDepSet.add(depListCopy.get(j));
                    currVerSet = verSet;
                }
            }

            int count = 0;
            TempSvc tempSvc = new TempSvc(currDep.getServiceName(), currVerSet);
            for (PureSvcDependency infectedDep : infectedDepSet) {
                count += depCount.get(infectedDep);
                depListCopy.remove(infectedDep);
                tempSvcMap.put(infectedDep, tempSvc);
            }
            tempSvcCount.put(tempSvc, count);
        }
    }
    @Deprecated
    public static void mappingSlaDepList(
            Map<PureSvcDependency, Integer> depCount, List<PureSvcDependency> depList, Map<TempSvc, Integer> verResult) {
        List<TempSvc> svcResultList = new ArrayList<>(verResult.keySet());
        svcResultList.sort(Comparator.comparingInt(verResult::get));

        for (PureSvcDependency slaDep : depList) {
            Set<MSvcVersion> verSet = new HashSet<>();
            for (int i = svcResultList.size() - 1; i >= 0; --i) {
                if (svcResultList.get(i).getServiceName().equals(slaDep.getServiceName())) {
                    for (MSvcVersion version : svcResultList.get(i).getVersionSet()) {
                        Optional<MService> svcOpt = svcManager.getByServiceNameAndVersion(svcResultList.get(i).getServiceName(), version.toString());
                        if (svcOpt.isPresent()) {
                            Optional<MSvcInterface> apiOpt = svcOpt.get().getInterfaceByPatternUrl(slaDep.getPatternUrl());
                            if (apiOpt.isPresent() && slaDep.getSlaSet().contains(apiOpt.get().getFuncDescription().getSla())) {
                                verSet.add(version);
                            }
                        }
                    }

                    if (!verSet.isEmpty()) {

                    }
                }
            }

            // todo: 如果没有在 ver dep 结果中找到合适的实例，就挑个最新版本
        }
    }
    @Deprecated
    public static List<MService> filterMappedSvc(List<MService> svcList, PureSvcDependency svcDependency) {
        List<MService> resultList = new ArrayList<>();
        for (MService svc : svcList) {
            BaseSvcDependency dep = BaseSvcDependency.tranPure(svcDependency);
            if (dep instanceof SvcVerDependency) {
                if (svc.getServiceName().equals(svcDependency.getServiceName())
                        && svcDependency.getVersionSet().contains(svc.getServiceVersion())) {
                    resultList.add(svc);
                }
            } else if (dep instanceof SvcSlaDependency) {
                if (svc.getServiceName().equals(svcDependency.getServiceName())) {
                    Optional<MSvcInterface> apiOpt = svc.getInterfaceByPatternUrl(svcDependency.getPatternUrl());
                    if (apiOpt.isPresent() && svcDependency.getSlaSet().contains(apiOpt.get().getFuncDescription().getSla())) {
                        resultList.add(svc);
                    }
                }
            } else if (dep instanceof SvcFuncDependency) {
                for (MSvcInterface api : svc.getServiceInterfaceMap().values()) {
                    for (MSla sla : svcDependency.getSlaSet()) {
                        if (api.getFuncDescription().ifSatisfied(svcDependency.getFunc(), sla)) {
                            resultList.add(svc);
                        }
                    }
                }
            }
        }
        return resultList;
    }
}
