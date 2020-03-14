package com.septemberhx.server.algorithm.dep.result;

import com.septemberhx.server.job.MBaseJob;
import com.septemberhx.server.model.MSvcInstManager;
import com.septemberhx.server.model.routing.MDepRoutingManager;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/14
 */
@Getter
@Setter
@ToString
public class MDepEvolveResult {
    private MSvcInstManager instManager;
    private MDepRoutingManager routingManager;

    public MDepEvolveResult(MSvcInstManager instManager, MDepRoutingManager routingManager) {
        this.instManager = instManager;
        this.routingManager = routingManager;
    }

    /*
     * Compare with the old instance locations and generating the jobs for evolution
     */
    public List<MBaseJob> compare(MSvcInstManager oldInstManager) {
        List<MBaseJob> jobList = new ArrayList<>();

        // todo: implement the job generation

        return jobList;
    }
}
