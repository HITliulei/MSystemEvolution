package com.septemberhx.common.bean.agent;

import com.netflix.appinfo.InstanceInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class MInstanceRegisterNotifyRequest {
    private String ip;
    private Integer port;
    private InstanceInfo instanceInfo;
}
