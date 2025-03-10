package com.techlabs.common.base.cache.factory;

import com.techlabs.common.base.cache.annotation.EnablePlatformCache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * Macrogen 프레임웍에서 DataSource를 생성하는데 사용하는 클래서.
 * DataSource를 위한 기본 설정값은 application.properties에서 조회한다.
 *
 * @author yjkim Yoo
 */
public class PlatformCacheManagerFactory extends AbstractFactoryBean<CacheManager> {
    private CacheManager instance;

    private EnablePlatformCache cacheConfig;

    public PlatformCacheManagerFactory(EnablePlatformCache cacheConfig) {
        this.cacheConfig = cacheConfig;
    }

    @Override
    public Class<?> getObjectType() {
        return CacheManager.class;
    }

    @Override
    protected CacheManager createInstance() throws Exception {
        if (instance != null) {
            return instance;
        }
        CacheManagerBuilder<CacheManager> builder = CacheManagerBuilder.newCacheManagerBuilder();
        if (this.cacheConfig.defaultMaxObjectCount() != -1)
        {
            builder = builder.withDefaultSizeOfMaxObjectGraph(this.cacheConfig.defaultMaxObjectCount());
        }
        if (this.cacheConfig.defaultMaxObjectSize() != -1)
        {
            builder = builder.withDefaultSizeOfMaxObjectSize(cacheConfig.defaultMaxObjectSize(), cacheConfig.maxObjectSizeUnit());
        }
        CacheManager cacheManager = builder.build(true);
        return cacheManager;
    }

    @Override
    protected void destroyInstance(CacheManager instance) throws Exception {
        if (this.instance != null) {
            this.instance.close();
        }
        this.instance = null;
    }
}
