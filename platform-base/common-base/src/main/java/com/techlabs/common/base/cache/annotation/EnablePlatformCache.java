package com.techlabs.common.base.cache.annotation;


import com.techlabs.common.base.cache.PlatformCacheBeanRegister;
import org.ehcache.config.units.MemoryUnit;
import org.springframework.context.annotation.Import;

import javax.management.NotificationListener;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Ehcache를 사용하기 쉽게 도와주는 용도의 클래스.
 *
 * @author yjkim
 */
@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({ PlatformCacheBeanRegister.class , NotificationListener.class })
public @interface EnablePlatformCache
{
    String cacheProvider() default "ehcache"; // ehcache , redis,

    long defaultMaxObjectCount() default -1L;

    long defaultMaxObjectSize() default -1L;

    MemoryUnit maxObjectSizeUnit() default MemoryUnit.B;

    long ttl() default Long.MAX_VALUE;

}
