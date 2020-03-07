package com.septemberhx.common.bean.server;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/7
 */
@ToString
@Getter
@Setter
public class MRedirectInfo {
    private String ip;
    private Integer port;
    private String patternUrl;
}
