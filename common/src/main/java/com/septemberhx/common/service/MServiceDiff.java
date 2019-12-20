package com.septemberhx.common.service;

import com.septemberhx.common.base.MUniqueObject;
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
public class MServiceDiff extends MUniqueObject {
    private String versionChange;
    private String portChange;
    private String urlChange;
    private String serviceNameChange;
    private List<MServiceInterfaceDiff> changeInterface;
    private List<MServiceInterface> addInterface;  // 增加的接口
    private List<MServiceInterface> reduceInterface;  // 减少的接口
}
