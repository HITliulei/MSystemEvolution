package com.septemberhx.common.service.diff;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/1/6
 */
public enum MDiffType {
    // --- service ---
    SERVICE_VERSION_DIFF,
    SERVICE_PORT_DIFF,
    SERVICE_PATH_DIFF,
    SERVICE_NAME_DIFF,


    // --- interface ---
    INTERFACE_METHEDNAME_DIFF,
    INTERFACE_REQUESTMETHOD_DIFF,
    INTERFACE_RETURNTYPE_DIFF,
    INTERFACE_FUNCTION_FEATURE_DIFF,
    INTERFACE_FUNCTION_LEVEL_DIFF,

    // ---- param ---
    PARAMER_REQUESTNAME_DIFF,
    PARAMER_TYPE_DIFF,
    PARAMER_REQUESTMETHOD_DIFF,
    PARAMER_DEFAULT_DIFF
}
