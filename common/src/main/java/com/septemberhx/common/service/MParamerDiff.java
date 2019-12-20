package com.septemberhx.common.service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Lei on 2019/12/17 18:40
 */

@Getter
@Setter
@ToString
public class MParamerDiff {
    private String type;   // 增加 / 减少  / 更新 / 无变化
    private String name;  //  名称
    private String requestname; // 请求名称
    private String dataType;  // 数据类型
    private String requestmethod;  //请求方式变更
    private String defaultvalue;  // 默认值
}
