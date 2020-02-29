package com.septemberhx.common.service.dependency;

import com.septemberhx.common.service.MSvcVersion;
import lombok.Getter;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/2/29
 */
@Getter
public class SvcVerDependency extends BaseSvcDependency {

    // service name
    private String serviceName;

    // pattern url for the API
    private String patternUrl;

    // the version of ${serviceName}
    private MSvcVersion version;

    public SvcVerDependency(String uniqueName, String serviceName, String patternUrl, MSvcVersion version) {
        this.name = uniqueName;
        this.serviceName = serviceName;
        this.patternUrl = patternUrl;
        this.version = version;
    }

    @Override
    public String toString() {
        return "SvcVerDependency{" +
                "serviceName='" + serviceName + '\'' +
                ", patternUrl='" + patternUrl + '\'' +
                ", version=" + version +
                ", name='" + name + '\'' +
                '}';
    }
}
