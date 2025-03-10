package com.techlabs.common.base.cache;

import com.techlabs.common.base.cache.factory.PlatformCacheFactory;
import com.techlabs.common.base.cache.factory.PlatformCacheManagerFactory;
import com.techlabs.platform.core.cache.CacheDataLoader;
import com.techlabs.common.base.cache.repository.EhcacheCommonRepository;
import com.techlabs.common.base.http.exception.PlatformInitializingFailedException;
import com.techlabs.common.base.utill.AnnotationUtil;
import com.techlabs.common.base.cache.annotation.EnablePlatformCache;
import com.techlabs.common.base.cache.annotation.PlatformEhcacheConfig;
import org.apache.commons.lang3.StringUtils;
import org.ehcache.Cache;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * EhcacheManager 및 Cache Bean을 등록하여 준다.
 * <p>
 * {@link EnablePlatformCache}에서 임포트 되어 사용됨.
 *
 * @author yjkim
 */
public class PlatformCacheBeanRegister implements ImportBeanDefinitionRegistrar
{
    private static final String CACHE_MANAGER_BEAN_NAME = "platformCacheManager";
    private static final String CACHE_FACTORY_BEAN_NAME = "platformCacheFactory";

    private EnablePlatformCache enablePlatformCache;
    private List<PlatformEhcacheConfig> cacheAnnotationList = new ArrayList<>();

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importMetadata, BeanDefinitionRegistry registry)
    {

        Map<String, Object> metaData = importMetadata.getAnnotationAttributes(EnablePlatformCache.class.getName());
        if (metaData == null)
        {
            throw new PlatformInitializingFailedException("No EnablePlatformCache annotation defined.");
        }

        String importingClassName = importMetadata.getClassName();
        String[] memberClassNames = importMetadata.getMemberClassNames(); // importing class의 하위에 있는 클래스들.

        enablePlatformCache = AnnotationUtil.findAnnotation(importingClassName, EnablePlatformCache.class);

        if (enablePlatformCache == null)
        {
            throw new PlatformInitializingFailedException("No CacheManager configuration");
        }

        // Config 클래스에 선언되어 있는 녀석들...
        PlatformEhcacheConfig
            cacheAnnotations[] = AnnotationUtil.findAnnotations(importingClassName, PlatformEhcacheConfig.class);
        for(PlatformEhcacheConfig annotation : cacheAnnotations)
        {
            cacheAnnotationList.add(annotation);
        }
        // Config Class의 하위 클래스에 선언된 녀석들.
        for(String cn : memberClassNames)
        {
            PlatformEhcacheConfig annotation = AnnotationUtil.findAnnotation(cn, PlatformEhcacheConfig.class);
            if (annotation == null)
            {
                continue;
            } else {
                cacheAnnotationList.add(annotation);
            }
        }

        // Bean 등록 시작!!
        registerCacheManager(registry);
        if (cacheAnnotationList.isEmpty() == false)
        {
            registerCache(registry);
        }
    }

    private void registerCache(BeanDefinitionRegistry registry)
    {
        try
        {
            BeanDefinition beanDefinition = registry.getBeanDefinition(CACHE_FACTORY_BEAN_NAME);
            if (StringUtils.equals(beanDefinition.getBeanClassName(), PlatformCacheFactory.class.getName()) == false)
            {
                throw new PlatformInitializingFailedException(
                    "Cannot register PlatformCacheFactory bean. beanName:" + CACHE_FACTORY_BEAN_NAME + " is already used.");
            }
        } catch (NoSuchBeanDefinitionException ex)
        {
            GenericBeanDefinition cacheFactoryDef = new GenericBeanDefinition();
            cacheFactoryDef.setBeanClassName(PlatformCacheFactory.class.getName());
            registry.registerBeanDefinition(CACHE_FACTORY_BEAN_NAME, cacheFactoryDef);
        }

        for (PlatformEhcacheConfig c : cacheAnnotationList)
        {
            // Cache Bean
            GenericBeanDefinition cacheDef = createCacheGenericBeanDefinition(CACHE_FACTORY_BEAN_NAME, c);
            registry.registerBeanDefinition(c.value(), cacheDef);

            // Repository Bean..
            GenericBeanDefinition repoDef = createCacheRepositoryGenericBeanDefinition(c);
            registry.registerBeanDefinition(c.value() + "Repository", repoDef);
        }
    }

    private GenericBeanDefinition createCacheGenericBeanDefinition(String factoryBeanName, PlatformEhcacheConfig c)
    {
        GenericBeanDefinition cacheDef = new GenericBeanDefinition();
        cacheDef.setBeanClassName(Cache.class.getName());

        ConstructorArgumentValues cacheFactoryArgs = new ConstructorArgumentValues();
        cacheFactoryArgs.addIndexedArgumentValue(0, new RuntimeBeanReference(CACHE_MANAGER_BEAN_NAME));
        cacheFactoryArgs.addIndexedArgumentValue(1, c);
        cacheDef.setConstructorArgumentValues(cacheFactoryArgs);

        cacheDef.setFactoryBeanName(factoryBeanName);
        cacheDef.setFactoryMethodName("createCache");

        return cacheDef;
    }

    private GenericBeanDefinition createCacheRepositoryGenericBeanDefinition(PlatformEhcacheConfig c)
    {
        GenericBeanDefinition repoDef = new GenericBeanDefinition();
        repoDef.setBeanClassName(EhcacheCommonRepository.class.getName());

        ConstructorArgumentValues constructorArgs = new ConstructorArgumentValues();
        constructorArgs.addIndexedArgumentValue(0, new RuntimeBeanReference(c.value()));
        if (c.dataLoader() != CacheDataLoader.class)
        {
            constructorArgs.addIndexedArgumentValue(1, new RuntimeBeanReference(c.dataLoader()));
        }
        repoDef.setConstructorArgumentValues(constructorArgs);
        repoDef.setInitMethodName("init");
        repoDef.setDestroyMethodName("clear");

        return repoDef;
    }


    private void registerCacheManager(BeanDefinitionRegistry registry)
    {
        try
        {
            BeanDefinition beanDefinition = registry.getBeanDefinition(CACHE_MANAGER_BEAN_NAME);
            if (StringUtils
                .equals(beanDefinition.getBeanClassName(), PlatformCacheManagerFactory.class.getName()) == false)
            {
                throw new PlatformInitializingFailedException(
                    "Cannot register macrogen cache manager. Bean Name:" + CACHE_MANAGER_BEAN_NAME + " is already used.");
            } else
            {
                return;
            }
        } catch (NoSuchBeanDefinitionException ex)
        {
            // Pass. EnablePlatformCache 어노테이션이 처음인 경우. 등록한다.
        }

        GenericBeanDefinition factoryDefinition = new GenericBeanDefinition();
        factoryDefinition.setBeanClassName(PlatformCacheManagerFactory.class.getName());

        ConstructorArgumentValues values = new ConstructorArgumentValues();
        values.addIndexedArgumentValue(0, enablePlatformCache);
        factoryDefinition.setConstructorArgumentValues(values);
        registry.registerBeanDefinition(CACHE_MANAGER_BEAN_NAME, factoryDefinition);
    }
}
