package com.techlabs.platform.core.cache;

import java.util.List;
import java.util.Map;

/**
 * 캐쉬 repository 기본 유형읠 정의 합니다.
 *
 * @param <K> key type class
 * @param <V> value type class
 *
 * @author yjkim
 * @since 2021.06
 */
public interface CacheRepository<K, V>
{
    void init();
    
    void dispose();

    V get(K key);

    List<V> getAll();

    Map<K, V> getAllMap();

    boolean find(K key);

    void put(K key, V val);

    void putAllMap(Map<K, V> map);

    void delete(K key);

    void clear();

    void refresh();
}
