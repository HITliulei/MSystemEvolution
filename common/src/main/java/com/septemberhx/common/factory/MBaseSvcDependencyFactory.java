package com.septemberhx.common.factory;

import com.septemberhx.common.service.MFunc;
import com.septemberhx.common.service.MSla;
import com.septemberhx.common.service.MSvcVersion;
import com.septemberhx.common.service.dependency.BaseSvcDependency;
import com.septemberhx.common.service.dependency.SvcFuncDependency;
import com.septemberhx.common.service.dependency.SvcSlaDependency;
import com.septemberhx.common.service.dependency.SvcVerDependency;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author Lei
 * @Date 2020/3/25 11:56
 * @Version 1.0
 */
public class MBaseSvcDependencyFactory {

    public static BaseSvcDependency createBaseSvcDependency(String dependeceId, String functionDescribe, Integer slas, String serviceName, String patternUrl,  List<String> versions){
        if(serviceName == null){
           return new SvcFuncDependency(
                    dependeceId,
                    functionDescribe==null?null:new MFunc(functionDescribe),
                    slas==null?null:new MSla(slas));
        }else{
            if(versions == null){
                return new SvcSlaDependency(
                        dependeceId,
                        serviceName,
                        slas==null?null:new MSla(slas),
                        patternUrl);
            }else{
                Set<MSvcVersion> set = new HashSet<>();
                for(String string:versions){
                    set.add(MSvcVersion.fromStr(string));
                }
                return new SvcVerDependency(
                        dependeceId,
                        serviceName,
                        patternUrl,
                        set);
            }
        }

    }
}
