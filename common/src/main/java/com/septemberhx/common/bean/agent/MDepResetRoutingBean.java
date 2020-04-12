package com.septemberhx.common.bean.agent;

import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.MSvcInstance;
import com.septemberhx.common.service.dependency.PureSvcDependency;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/4/13
 */
@Getter
@Setter
@ToString
public class MDepResetRoutingBean {

    private Map<String, MService> serviceMap;
    private Map<String, MSvcInstance> instMap;

    private List<PureSvcDependency> depList;
    private List<String> serviceIdList;

    public void putValue(PureSvcDependency dep, String svcId) {
        this.depList.add(dep);
        this.serviceIdList.add(svcId);
    }

    public Map<PureSvcDependency, String> depMap() {
        Map<PureSvcDependency, String> r = new HashMap<>();
        for (int i = 0; i < depList.size(); ++i) {
            r.put(this.depList.get(i), serviceIdList.get(i));
        }
        return r;
    }
}
