package com.septemberhx.common.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/10
 */
@Getter
@Setter
@ToString
public class MTimeIntervalBean {
    private long startTimeInMills;
    private long endTimeInMills;
}
