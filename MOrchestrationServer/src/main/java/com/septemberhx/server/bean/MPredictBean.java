package com.septemberhx.server.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/12
 */
@Getter
@Setter
public class MPredictBean {
    private Map<String, Map<Integer, List<Integer>>> data;

    public MPredictBean() {
        this.data = new HashMap<>();
    }
}
