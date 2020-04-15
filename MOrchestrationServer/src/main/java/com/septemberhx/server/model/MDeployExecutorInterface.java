package com.septemberhx.server.model;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/4/15
 */
public interface MDeployExecutorInterface {
    void execute();
    boolean checkIfFinished();
    void jobFinished(String jobId);
}
