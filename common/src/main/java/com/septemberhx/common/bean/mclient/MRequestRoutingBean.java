package com.septemberhx.common.bean.mclient;

import com.septemberhx.common.service.dependency.BaseSvcDependency;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/18
 */
@Getter
@Setter
@ToString
public class MRequestRoutingBean {
    private String clientId;
    private String userId;
    private BaseSvcDependency dependency;
    private String callerPatternUrl;
    private String gatewayNodeId;

    public MRequestRoutingBean(String clientId, String userId, BaseSvcDependency dependency, String gatewayNodeId) {
        this.clientId = clientId;
        this.userId = userId;
        this.dependency = dependency;
        this.gatewayNodeId = gatewayNodeId;
    }

    public MRequestRoutingBean() {
    }
}
