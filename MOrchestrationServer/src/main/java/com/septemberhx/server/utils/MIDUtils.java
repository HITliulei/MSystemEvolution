package com.septemberhx.server.utils;

import com.septemberhx.server.job.MJobType;
import org.apache.commons.lang.RandomStringUtils;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/1/6
 */
public class MIDUtils {
    private static Long lastJobIdTimeMills = 0L;
    private static int jobCountEachMill = 0;

    private static Long lastInstanceIdTimeMills = 0L;
    private static int instanceCountEachMill = 0;

    private final static String INSTANCE_ID_PREFIX = "INST";
    private final static String USER_ID_PREFIX = "USER";

    public static synchronized String uniqueJobId(MJobType jobType) {
        long currTimeMills = System.currentTimeMillis();
        if (currTimeMills != lastJobIdTimeMills) {
            MIDUtils.jobCountEachMill = 0;
            MIDUtils.lastJobIdTimeMills = currTimeMills;
        } else {
            ++MIDUtils.jobCountEachMill;
        }

        return String.format("%s_%s_%s", jobType.toString(), MIDUtils.lastJobIdTimeMills, MIDUtils.jobCountEachMill);
    }

    public static String uniqueInterfaceId(String serviceName, String interfaceName) {
        return String.format("%s_%s_%s", serviceName, interfaceName, RandomStringUtils.randomAlphanumeric(6));
    }

    public static synchronized String uniqueInstanceId(String serviceName, String version) {
        long currTimeMills = System.currentTimeMillis();
        if (currTimeMills != lastInstanceIdTimeMills) {
            MIDUtils.instanceCountEachMill = 0;
            MIDUtils.lastInstanceIdTimeMills = currTimeMills;
        } else {
            ++MIDUtils.instanceCountEachMill;
        }

        return String.format(
                "%s-%s-%s-%s", serviceName, version, MIDUtils.lastJobIdTimeMills, MIDUtils.instanceCountEachMill);
    }

    public static boolean checkIfUserId(String idStr) {
        return idStr != null && idStr.startsWith(USER_ID_PREFIX);
    }

    public static boolean checkIfInstId(String idStr) {
        return idStr != null && idStr.startsWith(INSTANCE_ID_PREFIX);
    }
}
