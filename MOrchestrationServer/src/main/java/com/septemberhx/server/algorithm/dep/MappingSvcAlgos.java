package com.septemberhx.server.algorithm.dep;

import com.septemberhx.common.service.MSvcVersion;
import com.septemberhx.common.service.dependency.PureSvcDependency;
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

    public static void mappingSvcDepList(Map<PureSvcDependency, Integer> depCount, List<PureSvcDependency> depList) {
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
}
