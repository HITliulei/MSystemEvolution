package com.septemberhx.common.bean.gateway;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author Lei
 * @Date 2020/3/31 16:04
 * @Version 1.0
 */
@Getter
@Setter
@ToString
public class MRequestUrl {
    private String ip;
    private String port;
    private String insterfaceName;

    public MRequestUrl(String ip, String port, String insterfaceName) {
        this.ip = ip;
        this.port = port;
        this.insterfaceName = insterfaceName;
    }
}
