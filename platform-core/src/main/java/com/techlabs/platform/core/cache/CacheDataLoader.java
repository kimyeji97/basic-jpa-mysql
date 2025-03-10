package com.techlabs.platform.core.cache;

import java.util.Map;

/**
 * @param <K> cache 데이터 로딩을 위한 interface.
 * @param <V> cache에 담길 값 타입.
 */
public interface CacheDataLoader<K, V>
{
    Map<K, V> load();
}
