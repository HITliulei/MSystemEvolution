package com.septemberhx.common.service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Lei on 2019/12/16 15:32
 */


@Getter
@Setter
@ToString
public class MParamer {
    private String name;  // 参数名称
    private String requestname;  // 请求的名称  若为requestBody 则为类名
    private String defaultObject;   // 默认值
    private String type; // 参数类型
    private String method; // 参数的请求方式   path / Paramer / requestBody
}
