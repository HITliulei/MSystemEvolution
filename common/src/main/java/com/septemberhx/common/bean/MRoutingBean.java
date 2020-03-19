package com.septemberhx.common.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/9
 */
@ToString
@Getter
@Setter
public class MRoutingBean {
    private String ipAddr;
    private Integer port;
    private String patternUrl;

    public MRoutingBean(String ipAddr, Integer port, String patternUrl) {
        this.ipAddr = ipAddr;
        this.port = port;
        this.patternUrl = patternUrl;
    }

    public MRoutingBean() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MRoutingBean that = (MRoutingBean) o;
        return Objects.equals(ipAddr, that.ipAddr) &&
                Objects.equals(port, that.port) &&
                Objects.equals(patternUrl, that.patternUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ipAddr, port, patternUrl);
    }
}
