package com.septemberhx.common.service.dependency;

import com.septemberhx.common.service.MSvcVersion;
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
public class SvcVerDependency extends BaseSvcDependency {

    public SvcVerDependency(String id, String serviceName, String patternUrl, Set<MSvcVersion> versionSet) {
        this.id = id;
        this.serviceName = serviceName;
        this.patternUrl = patternUrl;
        this.versionSet = versionSet;
    }
}
