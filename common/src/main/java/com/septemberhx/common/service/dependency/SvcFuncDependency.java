package com.septemberhx.common.service.dependency;

import com.septemberhx.common.service.MFunc;
import com.septemberhx.common.service.MSla;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/2/29
 */
@Getter
@Setter
@ToString
public class SvcFuncDependency extends BaseSvcDependency {

    public SvcFuncDependency(String id, String funcName, Integer sla) {
        this.id = id;
        PureSvcDependency dep = new PureSvcDependency();
        dep.setFunc(new MFunc(funcName));
        dep.setSla(new MSla(sla));
        this.setDep(dep);
    }

    public SvcFuncDependency(String id, MFunc func, MSla sla) {
        this.id = id;
        PureSvcDependency dep = new PureSvcDependency();
        dep.setFunc(func);
        dep.setSla(sla);
        this.setDep(dep);
    }

    public SvcFuncDependency(){

    }
}
