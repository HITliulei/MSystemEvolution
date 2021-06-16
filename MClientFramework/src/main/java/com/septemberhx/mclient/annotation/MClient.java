package com.septemberhx.mclient.annotation;

import com.septemberhx.mclient.config.Mvf4msConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(Mvf4msConfig.class)
public @interface MClient {
}