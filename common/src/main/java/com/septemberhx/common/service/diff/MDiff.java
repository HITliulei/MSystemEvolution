package com.septemberhx.common.service.diff;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/1/6
 *
 * List<MDiff> should be returned when comparing two services
 */
@Getter
@Setter
@ToString
public class MDiff {
    /**
     * Type of the difference
     */
    private MDiffType type;
    /**
     * Previous value
     */
    private Object preValue;

    /**
     * Current value
     */
    private Object curValue;

    public MDiff(MDiffType type, Object preValue, Object curValue) {
        this.type = type;
        this.preValue = preValue;
        this.curValue = curValue;
    }

    public MDiff(){

    }
}
