package com.septemberhx.server.algorithm.dep;

import com.septemberhx.common.bean.gateway.MDepRequestCacheBean;
import com.septemberhx.common.service.dependency.PureSvcDependency;
import com.septemberhx.server.config.MControlConfig;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/11
 *
 * One node user dependency demands prediction algorithms
 * Since this is a ML method, we will not implement the predictor in Java.
 *   Python is used instead, and the predictor is developed as a single service.
 * Java client only preprocesses the data and calls the APIs of the processor
 */
@Component
public class MDemandPredAlgo {

    @Autowired
    private MControlConfig controlConfig;

    /*
     * Value is BaseSvcDependency Object svcDependency
     * Key is svcDependency.hashcode()
     */
    public Map<Integer, PureSvcDependency> hash2Dep;

    /*
     * Return value:
     * Map [
     *   Node Id:
     *   Map [
     *     dependency hashcode:
     *     List [
     *       Pair<timestamp in mills, count>
     *     ]
     *   ]
     * ]
     */
    public Map<Integer, List<Pair<Long, Integer>>> feedNodeRequestCaches(List<MDepRequestCacheBean> requestCacheBeans) {
        Map<Integer, List<Pair<Long, Integer>>> result = new HashMap<>();

        Collections.sort(requestCacheBeans);
        for (MDepRequestCacheBean cacheBean : requestCacheBeans) {
            int hashcode = cacheBean.getBaseSvcDependency().hashCode();
            if (!this.hash2Dep.containsKey(hashcode)) {
                this.hash2Dep.put(hashcode, cacheBean.getBaseSvcDependency().getDep());
            }

            if (!result.containsKey(hashcode)) {
                result.put(hashcode, new ArrayList<>());
            }

            List<Pair<Long, Integer>> targetList = result.get(hashcode);
            if (targetList.size() > 0) {
                Pair<Long, Integer> lastPair = targetList.get(targetList.size() - 1);
                if (lastPair.getValue0().equals(cacheBean.getTimestamp())) {
                    targetList.set(targetList.size() - 1, new Pair<>(cacheBean.getTimestamp(), lastPair.getValue1() + 1));
                } else {
                    targetList.add(new Pair<>(cacheBean.getTimestamp(), lastPair.getValue1() + 1));
                }
            } else {
                targetList.add(new Pair<>(cacheBean.getTimestamp(), 1));
            }
        }

        return result;
    }

    public MDemandPredAlgo() {
        this.hash2Dep = new HashMap<>();
    }
}
