package com.septemberhx.common.service.dependency;

import com.septemberhx.common.service.MFuncDescription;
import com.septemberhx.common.service.MSla;
import com.septemberhx.common.service.MSvcVersion;
import lombok.Getter;

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

    protected MFuncDescription funcDescription;

    // service name
    protected String serviceName;

    // prefer sla level
    protected MSla sla;

    // API url
    protected String patternUrl;

    // the version of ${serviceName}
    protected MSvcVersion version;

    public BaseSvcDependency toRealDependency() {
        if (this.funcDescription != null) {
            return new SvcFuncDependency(this.id, this.funcDescription);
        } else if (this.serviceName != null && this.patternUrl != null && this.version != null) {
            return new SvcVerDependency(this.id, this.serviceName, this.patternUrl, this.version);
        } else if (this.serviceName != null && this.patternUrl != null && this.sla != null) {
            return new SvcSlaDependency(this.id, this.serviceName, this.sla, this.patternUrl);
        } else {
            return null;
        }
    }
}
