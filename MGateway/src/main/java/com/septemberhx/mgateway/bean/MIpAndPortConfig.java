package com.septemberhx.mgateway.bean;

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
public class MIpAndPortConfig {
    private String ip;
    private Integer port;

    public MIpAndPortConfig(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }
}
