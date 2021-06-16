package com.septemberhx.common.service.dependency;

import com.septemberhx.common.service.MSla;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/2/29
 */
@Getter
@Setter
@ToString
public class SvcSlaDependency extends BaseSvcDependency {

    public SvcSlaDependency(String id, String serviceName, MSla sla, String patternUrl) {
        this.id = id;
        PureSvcDependency dep = new PureSvcDependency();
        dep.setServiceName(serviceName);
        dep.setSla(sla);
        dep.setPatternUrl(patternUrl);
        this.setDep(dep);
    }


    public SvcSlaDependency(){

    }
}
