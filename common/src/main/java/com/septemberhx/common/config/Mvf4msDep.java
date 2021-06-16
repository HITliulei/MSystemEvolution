package com.septemberhx.common.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/1
 */
@Getter
@Setter
@ToString
public class Mvf4msDep {
    private String id;
    private String serviceName;
    private String patternUrl;
    private List<String> versions;
    private String function;
    private Integer slas;

    // For json parsing
    public Mvf4msDep() {
    }
}
