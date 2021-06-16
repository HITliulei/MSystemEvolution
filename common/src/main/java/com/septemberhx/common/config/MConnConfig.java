package com.septemberhx.common.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/11
 */
@ToString
@Getter
@Setter
public class MConnConfig {
    private String ip;
    private Integer port;
}
