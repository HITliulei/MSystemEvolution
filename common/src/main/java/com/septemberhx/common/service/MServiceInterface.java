package com.septemberhx.common.service;

import com.septemberhx.common.base.MUniqueObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2019/12/13
 */
@Getter
@Setter
@ToString
public class MServiceInterface extends MUniqueObject {

    private String patternUrl;
    private MFuncDescription funcDescription;
    private String functionName;
    private String requestMethod;
//    private Map<String, String> parameterTypeMap;
    private List<MParamer> paramers;
    private String returnType;
    private String serviceId;
}
