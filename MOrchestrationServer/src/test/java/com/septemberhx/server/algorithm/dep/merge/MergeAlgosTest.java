package com.septemberhx.server.algorithm.dep.merge;

import com.septemberhx.common.service.MSvcVersion;
import com.septemberhx.common.service.dependency.PureSvcDependency;
import com.septemberhx.server.algorithm.dep.MergeAlgos;
import junit.framework.TestCase;
import org.javatuples.Pair;

import java.util.*;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/15
 */
public class MergeAlgosTest extends TestCase {

    public void testMergeSameSvcPatternUrlList() {
        String serviceName = "service-test";
        String patternUrl = "/s/test";
        MSvcVersion v1 = new MSvcVersion(1, 0, 0);
        MSvcVersion v2 = new MSvcVersion(2, 0, 0);
        MSvcVersion v3 = new MSvcVersion(3, 0, 0);
        MSvcVersion v4 = new MSvcVersion(4, 0, 0);
        MSvcVersion v5 = new MSvcVersion(5, 0, 0);

        Set<MSvcVersion> verSet1 = new HashSet<>();
        verSet1.add(v1);
        verSet1.add(v2);
        PureSvcDependency dep1 = new PureSvcDependency(null, serviceName, null, patternUrl, verSet1);

        Set<MSvcVersion> verSet2 = new HashSet<>();
        verSet2.add(v3);
        verSet2.add(v4);
        PureSvcDependency dep2 = new PureSvcDependency(null, serviceName, null, patternUrl, verSet2);

        Set<MSvcVersion> verSet3 = new HashSet<>();
        verSet3.add(v1);
        verSet3.add(v2);
        verSet3.add(v3);
        PureSvcDependency dep3 = new PureSvcDependency(null, serviceName, null, patternUrl, verSet3);

        Set<MSvcVersion> verSet4 = new HashSet<>();
        verSet4.add(v1);
        verSet4.add(v3);
        verSet4.add(v5);
        PureSvcDependency dep4 = new PureSvcDependency(null, serviceName, null, patternUrl, verSet4);

        List<PureSvcDependency> dependencyList = new ArrayList<>();
        dependencyList.add(dep1);
        dependencyList.add(dep2);
        dependencyList.add(dep3);
        dependencyList.add(dep4);

        Map<PureSvcDependency, Integer> depCountMap = new HashMap<>();
        depCountMap.put(dep1, 1);
        depCountMap.put(dep2, 5);
        depCountMap.put(dep3, 10);
        depCountMap.put(dep4, 15);

        Pair<Map<PureSvcDependency, PureSvcDependency>, Map<PureSvcDependency, Integer>> result
                = MergeAlgos.mergeDepList(depCountMap, dependencyList);

        Set<MSvcVersion> resultVerSet1 = new HashSet<>();
        resultVerSet1.add(v1);
        resultVerSet1.add(v2);
        PureSvcDependency r1 = new PureSvcDependency(null, serviceName, null, patternUrl, resultVerSet1);

        Set<MSvcVersion> resultVerSet2 = new HashSet<>();
        resultVerSet2.add(v3);
        PureSvcDependency r2 = new PureSvcDependency(null, serviceName, null, patternUrl, resultVerSet2);

        assertTrue(result.getValue0().containsKey(dep1));
        assertEquals(result.getValue0().get(dep1), r1);

        assertTrue(result.getValue0().containsKey(dep2));
        assertEquals(result.getValue0().get(dep2), r2);

        assertTrue(result.getValue0().containsKey(dep3));
        assertEquals(result.getValue0().get(dep3), r2);

        assertTrue(result.getValue0().containsKey(dep4));
        assertEquals(result.getValue0().get(dep4), r2);

        assertTrue(result.getValue1().containsKey(r1));
        assertEquals(result.getValue1().get(r1).intValue(), 1);

        assertTrue(result.getValue1().containsKey(r2));
        assertEquals(result.getValue1().get(r2).intValue(), 30);
    }
}