package com.techlabs.common.base.cache.annotation;

import com.techlabs.common.base.notification.PlatformNotificationListener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = { ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PlatformEhcacheListener
{
    // listener name
    String value();
    
    // listener service
    Class<?> service() default Class.class;
    
    Class<? extends PlatformNotificationListener> cacheChangedListener() default PlatformNotificationListener.class;
    
//	public CacheChangedListener cacheChangedListener(CacheService cacheService) {
//		return new CacheChangedListener(cacheService);
//	}
}
