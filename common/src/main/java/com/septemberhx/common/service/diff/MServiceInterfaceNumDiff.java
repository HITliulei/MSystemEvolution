package com.septemberhx.common.service.diff;

import com.septemberhx.common.service.MSvcInterface;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Lei on 2020/1/7 14:00
 */

@Getter
@Setter
@ToString
public class MServiceInterfaceNumDiff extends MServiceInterfaceDiff {
    private MSvcInterface mSvcInterface;
    public MServiceInterfaceNumDiff(MDiffInterface mDiffInterface){
        this.mDiffInterface = mDiffInterface;
    }
    public MServiceInterfaceNumDiff(){

    }
}
