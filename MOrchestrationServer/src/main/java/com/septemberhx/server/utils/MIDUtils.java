package com.septemberhx.server.utils;

import com.septemberhx.server.job.MJobType;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/1/6
 */
public class MIDUtils {
    private static Long lastJobIdTimeMills = 0L;
    private static int jobCountEachMill = 0;

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
}
