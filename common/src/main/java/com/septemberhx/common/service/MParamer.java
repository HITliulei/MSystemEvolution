package com.septemberhx.common.service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

/**
 * Created by Lei on 2019/12/16 15:32
 */


@Getter
@Setter
@ToString
public class MParamer {
    // 参数名称
    private String name;
    // 请求的名称  若为requestBody 则为类名
    private String requestname;
    // 默认值
    private String defaultObject;
    // 参数类型
    private String type;
    // 参数的请求方式   path / Paramer / requestBody
    private String method;

    public MParamer(){

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MParamer paramer = (MParamer) o;
        return Objects.equals(name, paramer.name) &&
                Objects.equals(requestname, paramer.requestname) &&
                Objects.equals(defaultObject, paramer.defaultObject) &&
                Objects.equals(type, paramer.type) &&
                Objects.equals(method, paramer.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, requestname, defaultObject, type, method);
    }
}
