package com.septemberhx.common.service.diff;

import com.septemberhx.common.service.MParamer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author Lei
 * @Date 2020/1/7 15:57
 * @Version 1.0
 */
@Setter
@Getter
@ToString
public class MParamerNumDiff extends MParamerDiff{
    private MParamer mParamer;
    public MParamerNumDiff(MDiffParamer mDiffParamer){
        this.mDiffParamer = mDiffParamer;
    }
}
