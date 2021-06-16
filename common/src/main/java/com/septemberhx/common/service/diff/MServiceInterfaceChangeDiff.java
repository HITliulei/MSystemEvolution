package com.septemberhx.common.service.diff;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Created by Lei on 2020/1/7 14:02
 */
@Getter
@Setter
@ToString
public class MServiceInterfaceChangeDiff extends MServiceInterfaceDiff {
    private String pathUrl;
    private List<MDiff> list;
    private List<MParamerDiff> paramerDiffs;
    public MServiceInterfaceChangeDiff(MDiffInterface mDiffInterface){
        this.mDiffInterface = mDiffInterface;
    }

    public MServiceInterfaceChangeDiff(){

    }
}
