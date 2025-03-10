package com.techlabs.common.base.utill;

import com.techlabs.common.base.cache.annotation.PlatformEhcacheConfig;
import com.techlabs.common.base.http.exception.PlatformInitializingFailedException;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;

/**
 * Ehcache 사용시 필요한 유틸 클래스.
 *
 * @author yjkim
 */
public class EhcacheUtil {

    public static boolean isMatchedMemoryUnitSuffix(char l, char bl, char lower, char upper)
    {
        if (CharUtils.isAsciiNumeric(bl))
        {
            return l == lower || l == upper;
        } else {
            return (l == 'b' || l == 'B') && (bl == lower || bl == upper);
        }
    }

    public static MemoryUnit getMemoryUnit(String size)
    {
        if (size == null || size.length() < 2)
        {
            return null;
        }

        int len = size.length();

        char last = size.charAt(len - 1);
        char blast = size.charAt(len - 2);

        if (len == 2 && CharUtils.isAsciiNumeric(blast) == false)
        {
            return null;
        }

        if (isMatchedMemoryUnitSuffix(last, blast, 'b','B'))
        {
            return MemoryUnit.B;
        }
        else if (isMatchedMemoryUnitSuffix(last, blast, 'k','K'))
        {
            return MemoryUnit.KB;
        }
        else if (isMatchedMemoryUnitSuffix(last, blast, 'm','M'))
        {
            return MemoryUnit.MB;
        }
        else if (isMatchedMemoryUnitSuffix(last, blast, 'g','G'))
        {
            return MemoryUnit.GB;
        }
        else if (isMatchedMemoryUnitSuffix(last, blast, 't','T'))
        {
            return MemoryUnit.TB;
        }
        else
        {
            // default.
            return MemoryUnit.KB;
        }
    }

    public static Long getSizeValue(String size)
    {
        if (size.length() < 2)
        {
            return null;
        }

        int pos = -1;
        for(int i = 0 , len = size.length() ; i < len ; i++)
        {
            if (CharUtils.isAsciiNumeric(size.charAt(i)) == false)
            {
                pos = i;
                break;
            }
        }
        if (pos == -1)
        {
            return null;
        }

        return Long.parseLong(size.substring(0,pos));
    }


    /**
     * 별도의 캐쉬 설정을 생성하여 줍니다.
     *
     * @return
     */
    public static CacheConfiguration createCacheConfiguration(PlatformEhcacheConfig cc) {
        String heap = cc.heap();
        String offheap = cc.offheap();
        String disk = cc.disk();
        Long maxObjectCount = cc.maxObjectCount();

        if (StringUtils.isEmpty(heap) && StringUtils.isEmpty(offheap) && StringUtils.isEmpty(disk))
        {
            throw new PlatformInitializingFailedException("Cannot create CacheConfiguration because of no size configuration.");
        }

        ResourcePoolsBuilder resourcePoolsBuilder = ResourcePoolsBuilder.newResourcePoolsBuilder();
        if (StringUtils.isNotEmpty(heap)) {
            MemoryUnit unit = getMemoryUnit(heap);
            Long size = getSizeValue(heap);
            if (unit == null || size == null)
            {
                throw new PlatformInitializingFailedException("Cannot initialize heap size of the ehcache.");
            }
            resourcePoolsBuilder = resourcePoolsBuilder.heap(size, unit);
        }

        if (StringUtils.isNotEmpty(offheap)) {
            MemoryUnit unit = getMemoryUnit(offheap);
            Long size = getSizeValue(offheap);
            if (unit == null || size == null)
            {
                throw new PlatformInitializingFailedException("Cannot initialize offheap size of the ehcache.");
            }
            resourcePoolsBuilder = resourcePoolsBuilder.offheap(size, unit);
        }
        if (StringUtils.isNotEmpty(disk)) {
            MemoryUnit unit = getMemoryUnit(disk);
            Long size = getSizeValue(disk);
            if (unit == null || size == null)
            {
                throw new PlatformInitializingFailedException("Cannot initialize disk size of the ehcache.");
            }
            resourcePoolsBuilder = resourcePoolsBuilder.disk(size, unit, false);
        }

        CacheConfiguration cfg = CacheConfigurationBuilder.newCacheConfigurationBuilder(cc.keyType(), cc.valueType(),resourcePoolsBuilder)
                .withSizeOfMaxObjectGraph(maxObjectCount == Long.MAX_VALUE ? 1000 : maxObjectCount)
                .build();

        return cfg;
    }
}
