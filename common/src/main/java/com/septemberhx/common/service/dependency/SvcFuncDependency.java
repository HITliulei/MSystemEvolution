package com.septemberhx.common.service.dependency;

import com.septemberhx.common.service.MFunc;
import com.septemberhx.common.service.MSla;
import lombok.Getter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/2/29
 */
@Getter
@ToString
public class SvcFuncDependency extends BaseSvcDependency {

    public SvcFuncDependency(String id, String funcName, Set<Integer> slaIntSet) {
        this.id = id;
        PureSvcDependency dep = new PureSvcDependency();
        dep.setFunc(new MFunc(funcName));
        Set<MSla> slaSet = new HashSet<>();
        slaIntSet.forEach(sInt -> slaSet.add(new MSla(sInt)));
        dep.setSlaSet(slaSet);
        this.setDep(dep);
    }

    public SvcFuncDependency(String id, MFunc func, Set<MSla> slaSet) {
        this.id = id;
        PureSvcDependency dep = new PureSvcDependency();
        dep.setFunc(func);
        dep.setSlaSet(slaSet);
        this.setDep(dep);
    }
}
