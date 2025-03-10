package com.techlabs.common.base.cache.factory;

import com.techlabs.common.base.utill.EhcacheUtil;
import com.techlabs.common.base.cache.annotation.PlatformEhcacheConfig;
import org.ehcache.Cache;
import org.ehcache.CacheManager;

public class PlatformCacheFactory
{
    public Cache createCache(CacheManager cacheManager, PlatformEhcacheConfig cacheConfig)
    {
        return cacheManager.createCache(cacheConfig.value(), EhcacheUtil.createCacheConfiguration(cacheConfig));
    }
}
