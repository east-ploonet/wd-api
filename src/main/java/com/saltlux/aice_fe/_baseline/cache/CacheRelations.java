package com.saltlux.aice_fe._baseline.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheRelations {

    // When the cache corresponding to mapper class in from is updated, the cache of the current annotation mapper needs to be updated
    Class<?>[] from() default {};
    // When the current annotation label mapper's cache is updated, the cache corresponding to mapper class in to needs to be updated
    Class<?>[] to() default {};
}
