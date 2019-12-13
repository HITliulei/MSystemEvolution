package com.septemberhx.common.service;

import com.septemberhx.common.base.MUniqueObject;

import java.util.Map;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2019/12/13
 */
public class MServiceInterface extends MUniqueObject {

    private String patternUrl;
    private MFuncDescription funcDescription;
    private String functionName;
    private String requestMethod;
    private Map<String, String> parameterTypeMap;
    private String returnType;
    private String serviceId;

}
