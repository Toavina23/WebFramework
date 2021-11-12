package com.toavina.WebFramework.annotation;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(UrlMaps.class)
public @interface UrlMap {
    String methodName () default "";
    String mappedUrl () default "";
    String mappedView () default "";
}

