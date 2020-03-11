package com.septemberhx.common.service.dependency;

import com.septemberhx.common.config.Mvf4msDep;
import com.septemberhx.common.service.MFunc;
import com.septemberhx.common.service.MSla;
import com.septemberhx.common.service.MSvcVersion;
import lombok.Getter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/2/29
 *
 * Detail at the WIKI page of the repo.
 */
@Getter
@ToString
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

    // Coefficient for calculating user number
    // It stands for the average calling count for one request
    protected Integer coefficient = 1;

    public BaseSvcDependency toRealDependency() {
        if (this.func != null && this.slaSet != null && !this.slaSet.isEmpty()) {
            return new SvcFuncDependency(this.id, this.func, this.slaSet);
        } else if (this.serviceName != null && !this.serviceName.isEmpty()
                && this.patternUrl != null && !this.patternUrl.isEmpty()
                && this.versionSet != null && !this.versionSet.isEmpty()) {
            return new SvcVerDependency(this.id, this.serviceName, this.patternUrl, this.versionSet);
        } else if (this.serviceName != null && !this.serviceName.isEmpty()
                && this.patternUrl != null && !this.patternUrl.isEmpty()
                && this.slaSet != null && !this.slaSet.isEmpty()) {
            return new SvcSlaDependency(this.id, this.serviceName, this.slaSet, this.patternUrl);
        } else {
            return null;
        }
    }

    /*
     * This is really dangerous to mix the base class with children.
     *   Wrong usage will lead to null class attribute variables !!!
     */
    public static BaseSvcDependency tranConfig2Dependency(Mvf4msDep depConfig) {
        BaseSvcDependency dependency = new BaseSvcDependency();
        dependency.id = depConfig.getId();
        dependency.func = new MFunc(depConfig.getFunction());
        dependency.serviceName = depConfig.getServiceName();
        dependency.patternUrl = depConfig.getPatternUrl();

        if (depConfig.getSlas() != null) {
            dependency.slaSet = new HashSet<>();
            depConfig.getSlas().forEach(sInt -> dependency.slaSet.add(new MSla(sInt)));
        }

        if (depConfig.getVersions() != null) {
            dependency.versionSet = new HashSet<>();
            depConfig.getVersions().forEach(verStr -> dependency.versionSet.add(MSvcVersion.fromStr(verStr)));
        }

        return dependency;
    }
}
