package com.septemberhx.server.utils;

import com.septemberhx.server.job.MJobType;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/1/6
 */
public class MIDUtilsTest {
//    @Test
    public void uniqueJobId() {
        Set<String> uniqueJobIdSet = new HashSet<>();
        for (int i = 0; i < 10000; ++i) {
            String uniqueId = MIDUtils.uniqueJobId(MJobType.JOB_BUILD);
            assertFalse(uniqueJobIdSet.contains(uniqueId));
            uniqueJobIdSet.add(uniqueId);
        }
    }
}
