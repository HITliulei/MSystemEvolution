package com.septemberhx.common.service.diff;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Created by Lei on 2019/12/17 18:28
 */
@Getter
@Setter
@ToString
public class MServiceDiff {
    private List<MDiff> list ;

    private List<MServiceInterfaceDiff> mServiceInterfaceDiffs;

    public MServiceDiff(){

    }

}
