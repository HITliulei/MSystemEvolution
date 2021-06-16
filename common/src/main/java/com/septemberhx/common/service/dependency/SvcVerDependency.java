package com.septemberhx.common.service.dependency;

import com.septemberhx.common.service.MSvcVersion;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/2/29
 */
@Getter
@Setter
@ToString
public class SvcVerDependency extends BaseSvcDependency {

    public SvcVerDependency(String id, String serviceName, String patternUrl, Set<MSvcVersion> versionSet) {
        this.id = id;
        PureSvcDependency dep = new PureSvcDependency();
        dep.setServiceName(serviceName);
        dep.setPatternUrl(patternUrl);
        dep.setVersionSet(versionSet);
        this.setDep(dep);
    }

    public SvcVerDependency(){

    }
}
