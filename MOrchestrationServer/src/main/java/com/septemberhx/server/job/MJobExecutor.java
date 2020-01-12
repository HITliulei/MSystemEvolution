package com.septemberhx.server.job;

import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.exception.JobExecutionFailedException;
import com.septemberhx.common.utils.MRequestUtils;
import com.septemberhx.common.utils.MUrlUtils;
import com.septemberhx.server.utils.MServiceUtils;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/1/6
 *
 * Do different jobs, and only do the jobs
 */
public class MJobExecutor {
    public static void start(MBaseJob job) {
        if (job.getStatus() != MJobStatus.PENDING) {
            throw new JobExecutionFailedException(String.format("Cannot execute job: %s", job.toString()));
        }

        switch (job.getType()) {
            case JOB_BUILD:
                MJobExecutor.startBuildJob((MBuildJob) job);
                break;
            default:
                throw new JobExecutionFailedException(String.format("Unknown job type: %s", job.getType()));
        }
    }

    /**
     * Execute the build job. Just send the job to the build center.
     * @param buildJob: the target build job
     */
    private static void startBuildJob(MBuildJob buildJob) {
        MResponse r = MRequestUtils.sendRequest(
                MUrlUtils.getBuildCenterBuildUri(MServiceUtils.BUILD_CENTER_IP, MServiceUtils.BUILD_CENTER_PORT),
                buildJob.toJobBean(),
                MResponse.class,
                RequestMethod.POST
        );

        if (r == null) {
            throw new JobExecutionFailedException(String.format("Failed to send MBuildJob: %s", buildJob));
        } else {
            buildJob.markAsDoing();
        }
    }
}
