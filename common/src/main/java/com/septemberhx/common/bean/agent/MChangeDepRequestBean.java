package com.septemberhx.common.bean.agent;


import com.septemberhx.common.config.Mvf4msDepConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class MChangeDepRequestBean {
    private String serviceId;
    private List<Mvf4msDepConfig> svcDependencies;

    public MChangeDepRequestBean(String serviceId, List<Mvf4msDepConfig> svcDependency) {
        this.serviceId = serviceId;
        this.svcDependencies = svcDependency;
    }

    public MChangeDepRequestBean() { }
}
