package com.septemberhx.common.service.dependency;

import com.septemberhx.common.config.Mvf4msDep;
import com.septemberhx.common.service.MFunc;
import com.septemberhx.common.service.MSla;
import com.septemberhx.common.service.MSvcVersion;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/2/29
 *
 * Detail at the WIKI page of the repo.
 */
@Getter
@Setter
@ToString
public class BaseSvcDependency {

    // id which is used for mapping the request to the config
    // developer use the id to call APIs instead of embedded the service name and patternUrl in code
    protected String id;

    private PureSvcDependency dep = new PureSvcDependency();

    // Coefficient for calculating user number
    // It stands for the average calling count for one request
    protected Integer coefficient = 1;

    public BaseSvcDependency toRealDependency() {
        if (dep.func != null && dep.sla != null) {
            return new SvcFuncDependency(this.id, dep.func, dep.sla);
        } else if (dep.serviceName != null && !dep.serviceName.isEmpty()
                && dep.patternUrl != null && !dep.patternUrl.isEmpty()
                && dep.versionSet != null && !dep.versionSet.isEmpty()) {
            return new SvcVerDependency(this.id, dep.serviceName, dep.patternUrl, dep.versionSet);
        } else if (dep.serviceName != null && !dep.serviceName.isEmpty()
                && dep.patternUrl != null && !dep.patternUrl.isEmpty()
                && dep.sla != null ) {
            return new SvcSlaDependency(this.id, dep.serviceName, dep.sla, dep.patternUrl);
        } else {
            return null;
        }
    }

    public BaseSvcDependency(){

    }

    /*
     * This is really dangerous to mix the base class with children.
     *   Wrong usage will lead to null class attribute variables !!!
     */
    public static BaseSvcDependency tranConfig2Dependency(Mvf4msDep depConfig) {
        BaseSvcDependency dependency = new BaseSvcDependency();
        PureSvcDependency pureSvcDependency = new PureSvcDependency();
        dependency.id = depConfig.getId();
        pureSvcDependency.func = new MFunc(depConfig.getFunction());
        pureSvcDependency.serviceName = depConfig.getServiceName();
        pureSvcDependency.patternUrl = depConfig.getPatternUrl();

        if (depConfig.getSlas() != null) {
            pureSvcDependency.sla = new MSla(depConfig.getSlas());
        }

        if (depConfig.getVersions() != null) {
            pureSvcDependency.versionSet = new HashSet<>();
            depConfig.getVersions().forEach(verStr -> pureSvcDependency.versionSet.add(MSvcVersion.fromStr(verStr)));
        }
        dependency.setDep(pureSvcDependency);
        return dependency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseSvcDependency that = (BaseSvcDependency) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(dep, that.dep) &&
                Objects.equals(coefficient, that.coefficient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dep, coefficient);
    }

    public String getServiceName() {
        return this.dep.getServiceName();
    }

    public void setServiceName(String s) {
        this.dep.setServiceName(s);
    }

    public String getPatternUrl() {
        return this.dep.getPatternUrl();
    }

    public void setPatternUrl(String s) {
        this.dep.setPatternUrl(s);
    }

    public MFunc getFunc() {
        return this.dep.getFunc();
    }

    public void setFunc(MFunc func) {
        this.dep.setFunc(func);
    }

    public MSla getSla() {
        return this.dep.getSla();
    }

    public void setSlaSet(MSla sla) {
        this.dep.setSla(sla);
    }

    public Set<MSvcVersion> getVersionSet() {
        return this.dep.getVersionSet();
    }

    public void setVersionSet(Set<MSvcVersion> versionSet) {
        this.dep.setVersionSet(versionSet);
    }
}
