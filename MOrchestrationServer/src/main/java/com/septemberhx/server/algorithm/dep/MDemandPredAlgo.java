package com.septemberhx.server.algorithm.dep;

import com.septemberhx.common.bean.gateway.MDepRequestCacheBean;
import com.septemberhx.common.config.MConfig;
import com.septemberhx.common.service.dependency.PureSvcDependency;
import com.septemberhx.common.utils.MRequestUtils;
import com.septemberhx.common.utils.MUrlUtils;
import com.septemberhx.server.bean.MPredictBean;
import com.septemberhx.server.config.MControlConfig;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.net.URI;
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

    public Map<Integer, List<Integer>> formatDataOneNode(
            List<MDepRequestCacheBean> requestCacheBeans, long startMills, long intervalMills, long endMills) {
        Map<Integer, List<Integer>> resultList = new HashMap<>();
        Map<Integer, List<MDepRequestCacheBean>> tmpMap = new HashMap<>();

        for (MDepRequestCacheBean cacheBean : requestCacheBeans) {
            int hashcode = cacheBean.getBaseSvcDependency().hashCode();
            if (!tmpMap.containsKey(hashcode)) {
                tmpMap.put(hashcode, new ArrayList<>());
            }
            tmpMap.get(hashcode).add(cacheBean);
        }
        for (Integer dep : tmpMap.keySet()) {
            Collections.sort(tmpMap.get(dep));
        }

        for (Integer dep : tmpMap.keySet()) {
            resultList.put(dep, new ArrayList<>());

            int count = 0;
            int index = 0;
            long start = startMills;
            while (index < tmpMap.get(dep).size() && start < endMills) {
                if (tmpMap.get(dep).get(index).getTimestamp() < start + intervalMills) {
                    count += 1;
                    ++index;
                } else {
                    resultList.get(dep).add(count);
                    start += intervalMills;
                    count = 0;
                }
            }
            resultList.get(dep).add(count);
        }

        return resultList;
    }

    /*
     * Format all the request caches on each nodes to MPredictBean object
     */
    public MPredictBean formatDataAllNodes(
            Map<String, List<MDepRequestCacheBean>> data, long startMills, long intervalMills, long endMills) {
        Map<String, Map<Integer, List<Integer>>> formatedData = new HashMap<>();
        for (String nodeId : data.keySet()) {
            formatedData.put(nodeId, this.formatDataOneNode(data.get(nodeId), startMills, intervalMills, endMills));
        }
        MPredictBean result = new MPredictBean();
        result.setData(formatedData);
        return result;
    }

    /*
     * Use MDPredictor service to train and predict
     */
    public MPredictBean predict(
            Map<String, List<MDepRequestCacheBean>> data, long startMills, long intervalMills, long endMills) {
        MPredictBean dataBean = this.formatDataAllNodes(data, startMills, intervalMills, endMills);
        URI uri = MUrlUtils.getRemoteUri(
                this.controlConfig.getPredictor().getIp(),
                this.controlConfig.getPredictor().getPort(),
                MConfig.MDPREDICTOR_PREDICT
        );
        return MRequestUtils.sendRequest(uri, dataBean, MPredictBean.class, RequestMethod.POST);
    }

    public MDemandPredAlgo() {
        this.hash2Dep = new HashMap<>();
    }
}
