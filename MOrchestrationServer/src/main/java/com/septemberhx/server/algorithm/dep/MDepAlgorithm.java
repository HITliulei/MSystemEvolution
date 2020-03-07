package com.septemberhx.server.algorithm.dep;

import com.septemberhx.common.service.dependency.BaseSvcDependency;
import com.septemberhx.server.model.MServiceInstance;
import com.septemberhx.server.model.MSystemModel;

import java.util.Optional;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/7
 */
public class MDepAlgorithm {

    public static Optional<MServiceInstance> getAvailableInstForDepRequest(
            BaseSvcDependency baseSvcDependency, MSystemModel currModel) {
        BaseSvcDependency svcDependency = baseSvcDependency.toRealDependency();


        return Optional.empty();
    }
}
