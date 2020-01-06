package com.septemberhx.server.job;

import com.septemberhx.common.bean.server.MBuildJobBean;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/1/6
 */
@Getter
@Setter
@ToString
public class MBuildJob extends MBaseJob {

    private String serviceName;
    private String gitUrl;
    private String gitTag;
    private String serviceId;

    public MBuildJob(String serviceName, String gitUrl, String gitTag, String serviceId) {
        super(MJobType.JOB_BUILD);
        this.serviceName = serviceName;
        this.gitTag = gitTag;
        this.gitUrl = gitUrl;
        this.serviceId = serviceId;
    }

    public MBuildJobBean toJobBean() {
        MBuildJobBean buildJobBean = new MBuildJobBean();
        buildJobBean.setId(this.id);
        buildJobBean.setGitTag(this.getGitTag());
        buildJobBean.setGitUrl(this.gitUrl);
        buildJobBean.setServiceName(this.serviceName);
        return buildJobBean;
    }
}
