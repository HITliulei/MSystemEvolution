package com.ll.mv4ms.annotation;

import java.lang.annotation.*;

/**
 * Created by Lei on 2019/12/28 17:07
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MFuncDescription {
    String value() default "";
    int lavel() default 1;
}