package com.techlabs.common.base.cache.repository;

import com.techlabs.platform.core.cache.AbstractCacheRepository;
import com.techlabs.platform.core.cache.CacheDataLoader;
import org.ehcache.Cache;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * EhCache를 사용하는 기본 Repository를 제공 합니다.
 *
 * @param <K>
 *            캐쉬 key class type.
 * @param <V>
 *            캐쉬 value class type.
 *
 * @author yjkim
 */
public class EhcacheCommonRepository<K, V> extends AbstractCacheRepository<K, V>
{
    private final Cache<K, V> cache;
    private CacheDataLoader<K, V> loader = null;

    private final Object lock = new Object();

    public EhcacheCommonRepository(Cache<K, V> cache)
    {
        this.cache = cache;
    }

    public EhcacheCommonRepository(Cache<K, V> cache, CacheDataLoader<K, V> loader)
    {
        this.cache = cache;
        this.loader = loader;
    }

    public void setDataLoader(CacheDataLoader<K, V> dl)
    {
        loader = dl;
    }

    @Override
    public V get(K key)
    {
        return cache.get(key);
    }

    @Override
    public List<V> getAll()
    {
        return StreamSupport.stream(cache.spliterator(), false).map(o -> o.getValue()).collect(Collectors.toList());
    }

    @Override
    public Map<K, V> getAllMap()
    {
        return StreamSupport.stream(cache.spliterator(), false)
            .collect(Collectors.toMap(Cache.Entry::getKey, Cache.Entry::getValue));
    }

    @Override
    public boolean find(K key)
    {
        return cache.containsKey(key);
    }

    @Override
    public void put(K key, V val)
    {
        synchronized (lock)
        {
            cache.remove(key);
            cache.put(key, val);
        }
    }

    @Override
    public void putAllMap(Map<K, V> map)
    {
        synchronized (lock)
        {
            cacheClear();
            cache.putAll(map);
        }
    }

    @Override
    public void delete(K key)
    {
        synchronized (lock)
        {
            cache.remove(key);
        }
    }

    @Override
    public void clear()
    {
        synchronized (lock)
        {
            cacheClear();
        }
    }

    @Override
    public void refresh()
    {
        if (loader == null)
        {
            return;
        }

        synchronized (lock)
        {
            cacheClear();
            Map<K, V> kvMap = loader.load();
            cache.putAll(kvMap); // FIXME java 8 <-> java 17 버전 에러
        }
    }

    private void cacheClear()
    {
        Set<K> keySet =
            StreamSupport.stream(cache.spliterator(), false).map(o -> o.getKey()).collect(Collectors.toSet());
        cache.removeAll(keySet);
        cache.clear();
    }
}
