package com.septemberhx.common.bean.server;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/23
 */
@Getter
@Setter
@ToString
public class MUpdateCopyInstBean {
    private Map<String, String> copyMap;

    public MUpdateCopyInstBean() {
    }

    public MUpdateCopyInstBean(Map<String, String> copyMap) {
        this.copyMap = copyMap;
    }
}
