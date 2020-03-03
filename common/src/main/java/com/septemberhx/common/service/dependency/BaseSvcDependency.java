package com.septemberhx.common.service.dependency;

import com.septemberhx.common.service.MFunc;
import com.septemberhx.common.service.MFuncDescription;
import com.septemberhx.common.service.MSla;
import com.septemberhx.common.service.MSvcVersion;
import lombok.Getter;

import java.util.Set;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/2/29
 *
 * Detail at the WIKI page of the repo.
 */
@Getter
public class BaseSvcDependency {

    // id which is used for mapping the request to the config
    // developer use the id to call APIs instead of embedded the service name and patternUrl in code
    protected String id;

    protected MFunc func;

    // service name
    protected String serviceName;

    // prefer sla level
    protected Set<MSla> slaSet;

    // API url
    protected String patternUrl;

    // the version of ${serviceName}
    protected Set<MSvcVersion> versionSet;

    public BaseSvcDependency toRealDependency() {
        if (this.func != null && this.slaSet != null) {
            return new SvcFuncDependency(this.id, this.func, this.slaSet);
        } else if (this.serviceName != null && this.patternUrl != null && this.versionSet != null) {
            return new SvcVerDependency(this.id, this.serviceName, this.patternUrl, this.versionSet);
        } else if (this.serviceName != null && this.patternUrl != null && this.slaSet != null) {
            return new SvcSlaDependency(this.id, this.serviceName, this.slaSet, this.patternUrl);
        } else {
            return null;
        }
    }
}
