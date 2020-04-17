package com.septemberhx.common.bean.gateway;

import com.septemberhx.common.service.dependency.PureSvcDependency;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/4/12
 */
@Getter
@Setter
@ToString
public class MDepRequestCountBean {
    private String nodeId;
    private List<PureSvcDependency> depList;
    private List<Integer> depUserCount;
    private List<String> idList;

    public MDepRequestCountBean() { }

    public void putValue(PureSvcDependency dep, Integer count) {
        this.depList.add(dep);
        this.depUserCount.add(count);
    }

    public void addId(String id) {
        this.idList.add(id);
    }

    public MDepRequestCountBean(String nodeId) {
        this.nodeId = nodeId;
        this.depList = new ArrayList<>();
        this.depUserCount = new ArrayList<>();
        this.idList = new ArrayList<>();
    }

    public Map<PureSvcDependency, Integer> depUserCountMap() {
        Map<PureSvcDependency, Integer> resultMap = new HashMap<>();
        for (int i = 0; i < depList.size(); ++i) {
            resultMap.put(depList.get(i), depUserCount.get(i));
        }
        return resultMap;
    }
}
