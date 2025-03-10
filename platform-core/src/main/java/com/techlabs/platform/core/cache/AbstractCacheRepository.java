package com.techlabs.platform.core.cache;

import java.util.List;
import java.util.Map;

public abstract class AbstractCacheRepository<K, V> implements CacheRepository<K, V>
{
    @Override
    public void init()
    {
        this.refresh();
    };

    @Override
    public void dispose()
    {
        this.clear();
    };

    @Override
    public abstract V get(K key);

    @Override
    public abstract List<V> getAll();

    @Override
    public abstract Map<K, V> getAllMap();

    @Override
    public abstract boolean find(K key);

    @Override
    public abstract void put(K key, V val);

    @Override
    public abstract void putAllMap(Map<K, V> map);

    @Override
    public abstract void delete(K key);

    @Override
    public abstract void clear();

    @Override
    public abstract void refresh();
}
