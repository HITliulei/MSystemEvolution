package com.septemberhx.common.service.diff;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @Author Lei
 * @Date 2020/1/7 16:00
 * @Version 1.0
 */
@Getter
@Setter
@ToString

public class MParamerChangeDiff extends MParamerDiff{
    private String paramerName;
    private List<MDiff> list;
    public MParamerChangeDiff(MDiffParamer mDiffParamer){
        this.mDiffParamer = mDiffParamer;
    }
}
