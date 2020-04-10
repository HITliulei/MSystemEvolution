package com.septemberhx.server.bean;

import com.septemberhx.common.bean.agent.MDeployPodRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/4/10
 */
@Getter
@Setter
@ToString
public class MTestDeployBean {
    private String clusterId;
    private MDeployPodRequest deployJob;
}
