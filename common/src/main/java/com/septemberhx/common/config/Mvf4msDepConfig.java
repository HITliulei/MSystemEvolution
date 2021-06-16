package com.septemberhx.common.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

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

    public Optional<Mvf4msDep> getDepById(String depId) {
        for (Mvf4msDep dep : dependence) {
            if (dep.getId().equals(depId)) {
                return Optional.of(dep);
            }
        }
        return Optional.empty();
    }
}
