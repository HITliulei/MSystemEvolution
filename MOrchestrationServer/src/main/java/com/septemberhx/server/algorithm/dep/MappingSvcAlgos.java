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

import java.util.*;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/15
 */
public class MappingSvcAlgos {

    public static MSvcManager svcManager;

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

    public static void buildSvcTree(List<MService> demandSvcList) {

    }

    public static Map<PureSvcDependency, MService> mappingFuncDepList(
            Map<PureSvcDependency, Integer> depCount, List<PureSvcDependency> depList) {
        Set<PureSvcDependency> depSet = new HashSet<>(depList);

        List<MService> svcList = svcManager.getAllValues();
        Map<MService, Set<PureSvcDependency>> metMap = new HashMap<>();
        for (MService svc : svcList) {
            Set<PureSvcDependency> meetDep = new HashSet<>();
            for (PureSvcDependency dep : depSet) {
                BaseSvcDependency tmpDep = BaseSvcDependency.tranPure(dep);
                if (tmpDep instanceof SvcFuncDependency) {
                    for (MSla sla : dep.getSlaSet()) {
                        if (svc.ifSatisfied(dep.getFunc(), sla)) {
                            meetDep.add(dep);
                            break;
                        }
                    }
                } else if (tmpDep instanceof SvcSlaDependency) {
                    if (svc.getServiceName().equals(dep.getServiceName())) {
                        Optional<MSvcInterface> apiOpt = svc.getInterfaceByPatternUrl(dep.getPatternUrl());
                        if (apiOpt.isPresent() && dep.getSlaSet().contains(apiOpt.get().getFuncDescription().getSla())) {
                            meetDep.add(dep);
                        }
                    }
                } else if (tmpDep instanceof SvcVerDependency) {
                    if (svc.getServiceName().equals(dep.getServiceName()) && dep.getVersionSet().contains(svc.getServiceVersion())) {
                        meetDep.add(dep);
                    }
                }
            }
            metMap.put(svc, meetDep);
        }

        Map<PureSvcDependency, MService> mapResult = new HashMap<>();
        while (!depSet.isEmpty()) {
            List<MService> targetSvcList = new ArrayList<>(metMap.keySet());
            targetSvcList.sort(Comparator.comparingInt(v -> metMap.get(v).stream().mapToInt(depCount::get).sum()));
            MService targetSvc = targetSvcList.get(targetSvcList.size() - 1);

            for (PureSvcDependency svcDependency : metMap.get(targetSvc)) {
                mapResult.put(svcDependency, targetSvc);
                depSet.remove(svcDependency);
            }
            for (Set<PureSvcDependency> tmpSet : metMap.values()) {
                tmpSet.removeAll(metMap.get(targetSvc));
            }
            metMap.remove(targetSvc);
        }
        return mapResult;
    }

    // ------> Abandoned plan below <------

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
