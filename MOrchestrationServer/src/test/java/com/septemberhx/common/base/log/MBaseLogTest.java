package com.septemberhx.common.base.log;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/3
 */
public class MBaseLogTest extends TestCase {

    public void testGetLogFromMap() {
        Map<String, Object> testMap = new HashMap<>();
        testMap.put("logType", "FUNCTION_CALL_END");
        testMap.put("logIpAddr", "10.244.0.1");
        testMap.put("logFromIpAddr", "10.244.0.1");
        testMap.put("logObjectId", "MGateway");
        testMap.put("logMethodName", "user_01_pay_function");
        testMap.put("logUserId", "user_01");
        testMap.put("logDateTimeInMills", 1574497241787L);
        testMap.put("logFromPort", 57845);

        MBaseLog baseLog = MBaseLog.getLogFromMap(testMap);
        assertNotNull(baseLog);
        System.out.println(baseLog.toString());
    }
}