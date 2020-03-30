package com.septemberhx.mclient.annotation;

import java.lang.annotation.*;

/**
 * Created by Lei on 2019/12/28 17:07
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MFuncDescription {
    String value() default "";
    int level() default 1;
}
