package com.septemberhx.server.job;


import com.septemberhx.common.service.dependency.BaseSvcDependency;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MChangeDepJob extends MBaseJob {
    private String serviceId;
    private BaseSvcDependency svcDependency;  // new deps

    public MChangeDepJob(String serviceId, BaseSvcDependency svcDependency) {
        super(MJobType.JOB_CHANGE_DEP);
        this.serviceId = serviceId;
        this.svcDependency = svcDependency;
    }
}
