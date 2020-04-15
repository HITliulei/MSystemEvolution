package com.septemberhx.common.bean;

import com.septemberhx.common.base.node.ServerNodeType;
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
    private ServerNodeType nodeType;

    public MRoutingBean(String ipAddr, Integer port, String patternUrl, ServerNodeType nodeType) {
        this.ipAddr = ipAddr;
        this.port = port;
        this.patternUrl = patternUrl;
        this.nodeType = nodeType;
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
                Objects.equals(patternUrl, that.patternUrl) &&
                Objects.equals(nodeType, that.nodeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ipAddr, port, patternUrl, nodeType);
    }
}
