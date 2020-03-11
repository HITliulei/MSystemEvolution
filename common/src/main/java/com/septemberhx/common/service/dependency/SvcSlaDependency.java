package com.septemberhx.common.service.dependency;

import com.septemberhx.common.service.MSla;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/2/29
 */
@Getter
@ToString
public class SvcSlaDependency extends BaseSvcDependency {

    public SvcSlaDependency(String id, String serviceName, Set<MSla> slaSet, String patternUrl) {
        this.id = id;
        PureSvcDependency dep = new PureSvcDependency();
        dep.setServiceName(serviceName);
        dep.setSlaSet(slaSet);
        dep.setPatternUrl(patternUrl);
        this.setDep(dep);
    }
}
