package com.septemberhx.mclient.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/1
 */
@Getter
@Setter
@ToString
public class Mvf4msDepConfig {
    private String name;
    private List<Mvf4msDep> dependence;
}
