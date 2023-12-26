package com.saltlux.aice_fe._baseline.cache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RelativeCacheContext {

    // Mapping relationship of storing full cache
    public static final Map<Class<?>, RelativeCache> MAPPER_CACHE_MAP = new ConcurrentHashMap<>();
    // Storing Mapper's corresponding cache requires to update the cache, but Mapper's corresponding cache has not been loaded at this time
    // That is, class <? > When the corresponding cache is updated, the cache in list < relativecache > needs to be updated
    public static final Map<Class<?>, List<RelativeCache>> UN_LOAD_TO_RELATIVE_CACHES_MAP = new ConcurrentHashMap<>();
    // The cache corresponding to Mapper needs to be updated from, but these caches are not loaded when Mapper cache is loaded
    // The cache in < relative list > needs to be updated Corresponding cache
    public static final Map<Class<?>, List<RelativeCache>> UN_LOAD_FROM_RELATIVE_CACHES_MAP = new ConcurrentHashMap<>();

    public static void putCache(Class<?> clazz, RelativeCache cache) {
        MAPPER_CACHE_MAP.put(clazz, cache);
    }

    public static void getCache(Class<?> clazz) {
        MAPPER_CACHE_MAP.get(clazz);
    }
}
