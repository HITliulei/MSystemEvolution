package com.septemberhx.server.algorithm.dep;

import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.MSla;
import com.septemberhx.common.service.MSvcInterface;
import com.septemberhx.common.service.MSvcVersion;
import com.septemberhx.common.service.dependency.*;
import com.septemberhx.common.utils.CommonUtils;
import com.septemberhx.server.model.MSvcManager;
import org.javatuples.Pair;

import java.util.*;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/15
 */
public class MergeAlgos {

    public static MSvcManager svcManager;

    public static Pair<Map<PureSvcDependency, PureSvcDependency>, Map<PureSvcDependency, Integer>>
        mergeDepList(Map<PureSvcDependency, Integer> demandCountMap, List<PureSvcDependency> verDepList) {
        Map<PureSvcDependency, Integer> demandCountMapCopy = new HashMap<>(demandCountMap);
        Map<PureSvcDependency, PureSvcDependency> resultMap = new HashMap<>();

        while (!verDepList.isEmpty()) {
            verDepList = sortDepListByTypeAndCount(demandCountMapCopy, verDepList);
            PureSvcDependency currDep = verDepList.get(verDepList.size() - 1);
            Set<PureSvcDependency> intersectedSet = new HashSet<>();
            intersectedSet.add(currDep);
            for (int j = verDepList.size() - 2; j >= 0; --j) {
                PureSvcDependency tmpDep = null;

                BaseSvcDependency _currDep = BaseSvcDependency.tranPure(currDep);
                BaseSvcDependency _nextDep = BaseSvcDependency.tranPure(verDepList.get(j));
                if (_currDep instanceof SvcFuncDependency) {
                    if (_nextDep instanceof SvcFuncDependency) {
                        tmpDep = _mergeFuncDepFunc(currDep, verDepList.get(j));
                    } else if (_nextDep instanceof SvcSlaDependency) {
                        tmpDep = _mergeFunc2Sla(currDep, verDepList.get(j));
                    } else if (_nextDep instanceof SvcVerDependency) {
                        tmpDep = _mergeFunc2Ver(currDep, verDepList.get(j));
                    }
                } else if (_currDep instanceof SvcSlaDependency) {
                    if (_nextDep instanceof  SvcFuncDependency) {
                        tmpDep = _mergeFunc2Sla(verDepList.get(j), currDep);
                    } else if (_nextDep instanceof SvcSlaDependency) {
                        tmpDep = _mergeSlaDepFunc(currDep, verDepList.get(j));
                    } else if (_nextDep instanceof SvcVerDependency) {
                        tmpDep = _mergeSla2Ver(currDep, verDepList.get(j));
                    }
                } else if (_currDep instanceof  SvcVerDependency) {
                    if (_nextDep instanceof SvcFuncDependency) {
                        tmpDep = _mergeFunc2Ver(verDepList.get(j), currDep);
                    } else if (_nextDep instanceof SvcSlaDependency) {
                        tmpDep = _mergeSla2Ver(verDepList.get(j), currDep);
                    } else if (_nextDep instanceof SvcVerDependency) {
                        tmpDep = _mergeVerDepFunc(currDep, verDepList.get(j));
                    }
                }

                if (tmpDep == null) {
                    continue;
                }

                intersectedSet.add(verDepList.get(j));
                currDep = tmpDep;
            }

            int replacedCount = 0;
            for (PureSvcDependency tmpDep : intersectedSet) {
                verDepList.remove(tmpDep);
                replacedCount += demandCountMapCopy.get(tmpDep);
                demandCountMapCopy.remove(tmpDep);
                resultMap.put(tmpDep, currDep);
            }
            demandCountMapCopy.put(currDep, replacedCount);
        }
        return new Pair<>(resultMap, demandCountMapCopy);
    }

    public static List<PureSvcDependency> sortDepListByTypeAndCount(
            Map<PureSvcDependency, Integer> countMap, List<PureSvcDependency> depList) {
        List<PureSvcDependency> resultList = new ArrayList<>();
        List<PureSvcDependency> verDepList = new ArrayList<>();
        List<PureSvcDependency> slaDepList = new ArrayList<>();
        List<PureSvcDependency> funcDepList = new ArrayList<>();

        for (PureSvcDependency svcDependency : depList) {
            BaseSvcDependency dependency = BaseSvcDependency.tranPure(svcDependency);
            if (dependency instanceof SvcVerDependency) {
                verDepList.add(svcDependency);
            } else if (dependency instanceof SvcSlaDependency) {
                slaDepList.add(svcDependency);
            } else if (dependency instanceof SvcFuncDependency) {
                funcDepList.add(svcDependency);
            }
        }
        verDepList.sort(Comparator.comparingInt(countMap::get));
        slaDepList.sort(Comparator.comparingInt(countMap::get));
        funcDepList.sort(Comparator.comparingInt(countMap::get));
        resultList.addAll(verDepList);
        resultList.addAll(slaDepList);
        resultList.addAll(funcDepList);
        return resultList;
    }

    public static PureSvcDependency _mergeVerDepFunc(PureSvcDependency v1, PureSvcDependency v2) {
        Set<MSvcVersion> verSet = CommonUtils.getSetIntersection(v1.getVersionSet(), v2.getVersionSet());
        if (verSet.isEmpty()) {
            return null;
        } else {
            return new PureSvcDependency(
                    v1.getFunc(),
                    v1.getServiceName(),
                    v1.getSlaSet(),
                    v1.getPatternUrl(),
                    verSet
            );
        }
    }

    public static PureSvcDependency _mergeSlaDepFunc(PureSvcDependency v1, PureSvcDependency v2) {
        Set<MSla> slaSet = CommonUtils.getSetIntersection(v1.getSlaSet(), v2.getSlaSet());
        if (slaSet.isEmpty()) {
            return null;
        } else {
            return new PureSvcDependency(
                    v1.getFunc(),
                    v1.getServiceName(),
                    slaSet,
                    v1.getPatternUrl(),
                    v1.getVersionSet()
            );
        }
    }

    public static PureSvcDependency _mergeFuncDepFunc(PureSvcDependency v1, PureSvcDependency v2) {
        Set<MSla> slaSet = CommonUtils.getSetIntersection(v1.getSlaSet(), v2.getSlaSet());
        if (slaSet.isEmpty()) {
            return null;
        } else {
            return new PureSvcDependency(
                    v1.getFunc(),
                    v1.getServiceName(),
                    slaSet,
                    v1.getPatternUrl(),
                    v1.getVersionSet()
            );
        }
    }

    /*
     * Merge Sla dep to Ver dep ==> Ver dep
     */
    public static PureSvcDependency _mergeSla2Ver(PureSvcDependency slaDep, PureSvcDependency verDep) {
        if (!slaDep.getServiceName().equals(verDep.getServiceName())
                || !slaDep.getPatternUrl().equals(verDep.getPatternUrl())) {
            return null;
        }

        Set<MSvcVersion> verSet = new HashSet<>();
        for (MSla sla : slaDep.getSlaSet()) {
            for (MSvcVersion ver : verDep.getVersionSet()) {
                Optional<MService> svcOpt = svcManager.getByServiceNameAndVersion(verDep.getServiceName(), ver.toString());
                if (svcOpt.isPresent()) {
                    Optional<MSvcInterface> apiOpt = svcOpt.get().getInterfaceByPatternUrl(verDep.getPatternUrl());
                    if (apiOpt.isPresent()) {
                        if (apiOpt.get().getFuncDescription().getSla().ifSatisfied(sla)) {
                            verSet.add(ver);
                        }
                    }
                }
            }
        }

        if (verSet.isEmpty()) {
            return null;
        } else {
            return new PureSvcDependency(
                    verDep.getFunc(),
                    verDep.getServiceName(),
                    verDep.getSlaSet(),
                    verDep.getPatternUrl(),
                    verSet
            );
        }
    }

    /*
     * Merge Func dep to Sla dep ==> Ver dep
     */
    public static PureSvcDependency _mergeFunc2Sla(PureSvcDependency funcDep, PureSvcDependency slaDep) {
        List<MService> serviceList = svcManager.getServicesBySlaDep(slaDep);
        Set<MSvcVersion> verSet = new HashSet<>();
        for (MService svc : serviceList) {
            Optional<MSvcInterface> apiOpt = svc.getInterfaceByPatternUrl(slaDep.getPatternUrl());
            if (apiOpt.isPresent()) {
                for (MSla sla : funcDep.getSlaSet()) {
                    if (apiOpt.get().getFuncDescription().ifSatisfied(funcDep.getFunc(), sla)) {
                        verSet.add(svc.getServiceVersion());
                        break;
                    }
                }
            }
        }

        if (verSet.isEmpty()) {
            return null;
        } else {
            return new PureSvcDependency(
                    null,
                    slaDep.getServiceName(),
                    null,
                    slaDep.getPatternUrl(),
                    verSet
            );
        }
    }

    public static PureSvcDependency _mergeFunc2Ver(PureSvcDependency funcDep, PureSvcDependency verDep) {
        List<MService> svcList = svcManager.getServicesByServiceName(verDep.getServiceName());
        Set<MSvcVersion> verSet = new HashSet<>();
        for (MService svc : svcList) {
            if (!verDep.getVersionSet().contains(svc.getServiceVersion())) {
                continue;
            }

            Optional<MSvcInterface> apiOpt = svc.getInterfaceByPatternUrl(verDep.getPatternUrl());
            if (apiOpt.isPresent()) {
                for (MSla sla : funcDep.getSlaSet()) {
                    if (apiOpt.get().getFuncDescription().ifSatisfied(funcDep.getFunc(), sla)) {
                        verSet.add(svc.getServiceVersion());
                    }
                }
            }
        }

        if (verSet.isEmpty()) {
            return null;
        } else {
            return new PureSvcDependency(
                    verDep.getFunc(),
                    verDep.getServiceName(),
                    verDep.getSlaSet(),
                    verDep.getPatternUrl(),
                    verSet
            );
        }
    }
}
