package com.septemberhx.server.algorithm.dep;

import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.bean.gateway.MDepRequestCacheBean;
import com.septemberhx.common.service.MSla;
import com.septemberhx.common.service.dependency.BaseSvcDependency;
import junit.framework.TestCase;
import org.javatuples.Pair;

import java.util.*;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/11
 */
public class MDemandPredAlgoTest extends TestCase {

    public void testFeedNodeRequestCaches() {
        MDemandPredAlgo algo = new MDemandPredAlgo();

        List<MDepRequestCacheBean> beanList = new ArrayList<>();
        BaseSvcDependency svcDependency = new BaseSvcDependency();
        svcDependency.setId("dep-1");
        svcDependency.setServiceName("service-1");
        svcDependency.setPatternUrl("/s/1");
        Set<MSla> slaSet = new HashSet<>();
        slaSet.add(new MSla(1));
        svcDependency.setSlaSet(slaSet);
        MDepRequestCacheBean bean1 = new MDepRequestCacheBean(
                svcDependency, "user-01", 0, MResponse.successResponse()
        );
        beanList.add(bean1);

        BaseSvcDependency svcDependency1 = new BaseSvcDependency();
        svcDependency1.setId("dep-1");
        svcDependency1.setServiceName("service-1");
        svcDependency1.setPatternUrl("/s/1");
        Set<MSla> slaSet1 = new HashSet<>();
        slaSet1.add(new MSla(1));
        svcDependency1.setSlaSet(slaSet1);
        MDepRequestCacheBean bean2 = new MDepRequestCacheBean(
                svcDependency1, "user-01", 0, MResponse.successResponse()
        );
        beanList.add(bean2);

        BaseSvcDependency svcDependency2 = new BaseSvcDependency();
        svcDependency2.setId("dep-1");
        svcDependency2.setServiceName("service-1");
        svcDependency2.setPatternUrl("/s/1");
        Set<MSla> slaSet2 = new HashSet<>();
        slaSet2.add(new MSla(1));
        svcDependency2.setSlaSet(slaSet2);
        MDepRequestCacheBean bean3 = new MDepRequestCacheBean(
                svcDependency2, "user-01", 1, MResponse.successResponse()
        );
        beanList.add(bean3);

        Map<Integer, List<Pair<Long, Integer>>> r = algo.feedNodeRequestCaches(beanList);
        assertEquals(1, r.size());
        assertEquals(2, r.get(svcDependency.hashCode()).size());
        assertEquals(2, r.get(svcDependency1.hashCode()).get(0).getValue1().intValue());
        assertEquals(3, r.get(svcDependency2.hashCode()).get(1).getValue1().intValue());
    }
}