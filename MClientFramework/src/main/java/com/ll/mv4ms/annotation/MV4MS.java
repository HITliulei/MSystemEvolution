package com.ll.mv4ms.annotation;

import com.ll.mv4ms.base.MV4MSconfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Author Lei
 * @Date 2020/3/16 13:35
 * @Version 1.0
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MV4MSconfig.class)
public @interface MV4MS { }
