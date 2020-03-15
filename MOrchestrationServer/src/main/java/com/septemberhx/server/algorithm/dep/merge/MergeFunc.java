package com.septemberhx.server.algorithm.dep.merge;

import com.septemberhx.common.service.dependency.PureSvcDependency;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/15
 */
@FunctionalInterface
public interface MergeFunc {

    /*
     * Merge two dependency. Null will be return if cannot merged
     */
    PureSvcDependency merge(PureSvcDependency v1, PureSvcDependency v2);
}
