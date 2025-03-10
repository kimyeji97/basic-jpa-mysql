package com.techlabs.common.base.cache.annotation;

import com.techlabs.platform.core.cache.CacheDataLoader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Cache Manager에 속한 각 캐쉬풀의 설정값!
 */
@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PlatformEhcacheConfig
{
    // cache name.
    String value();

    Class<?> keyType() default String.class;

    Class<?> valueType() default String.class;

    // ehcache storage size configuration
    String heap() default "1M";

    String disk() default "";

    String offheap() default "";

    Class<? extends CacheDataLoader> dataLoader() default CacheDataLoader.class;

    long maxObjectCount() default Long.MAX_VALUE;
}
