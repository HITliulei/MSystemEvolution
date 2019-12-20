package com.septemberhx.common.service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Created by Lei on 2019/12/17 18:33
 */

@Getter
@Setter
@ToString
public class MServiceInterfaceDiff {
    private String pathurl;
    private String methodnameChange;
    private String requestMethodChange;
    private String returnTypeChange;
    private String functionDiscribe;
    private String slaLevelDiff;
    private List<MParamerDiff> parameChanges;

}
