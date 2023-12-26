package com.saltlux.aice_fe._baseline.cache;

import org.apache.ibatis.cache.Cache;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.saltlux.aice_fe._baseline.cache.RelativeCacheContext.*;

public class RelativeCache implements Cache {

    private Map<Object, Object> CACHE_MAP = new ConcurrentHashMap<>();

    private List<RelativeCache> relations = new ArrayList<>();

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

    private String id;
    private Class<?> mapperClass;
    private boolean clearing;

    public RelativeCache(String id) throws Exception {
        this.id = id;
        this.mapperClass = Class.forName(id);
        RelativeCacheContext.putCache(mapperClass, this);
        loadRelations();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void putObject(Object key, Object value) {
        if (null != value) {
            CACHE_MAP.put(key, value);
        }
    }

    @Override
    public Object getObject(Object key) {
        return CACHE_MAP.get(key);
    }

    @Override
    public Object removeObject(Object key) {
        return CACHE_MAP.remove(key);
    }

    @Override
    public void clear() {
        ReadWriteLock readWriteLock = getReadWriteLock();
        Lock lock = readWriteLock.writeLock();
        lock.lock();
        try {
            // Judge whether the current cache is being emptied. If it is, cancel the operation
            // Avoid circular relation in the cache, resulting in non termination of recursion and overflow of call stack
            if (clearing) {
                return;
            }
            clearing = true;
            try {
                CACHE_MAP.clear();
                relations.forEach(RelativeCache::clear);
            } finally {
                clearing = false;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getSize() {
        return CACHE_MAP.size();
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return readWriteLock;
    }

    public void addRelation(RelativeCache relation) {
        if (relations.contains(relation)){
            return;
        }
        relations.add(relation);
    }

    void loadRelations() {
        // When loading other cache updates, you need to update the caches of this cache
        // Add this cache to the relations hips of these caches
        List<RelativeCache> to = UN_LOAD_TO_RELATIVE_CACHES_MAP.get(mapperClass);
        if (to != null) {
            to.forEach(relativeCache -> this.addRelation(relativeCache));
        }
        // Some cache caches that need to be updated when loading this cache update
        // Add these cache caches to this cache relations hip
        List<RelativeCache> from = UN_LOAD_FROM_RELATIVE_CACHES_MAP.get(mapperClass);
        if (from != null) {
            from.forEach(relativeCache -> relativeCache.addRelation(this));
        }

        CacheRelations annotation = AnnotationUtils.findAnnotation(mapperClass, CacheRelations.class);
        if (annotation == null) {
            return;
        }

        Class<?>[] toMappers = annotation.to();
        Class<?>[] fromMappers = annotation.from();

        if (toMappers != null && toMappers.length > 0) {
            for (Class c : toMappers) {
                RelativeCache relativeCache = MAPPER_CACHE_MAP.get(c);
                if (relativeCache != null) {
                    // Add the found cache to the relations hips of the current cache
                    this.addRelation(relativeCache);
                } else {
                    // If the to cache cannot be found, it proves that the to cache has not been loaded. At this time, the corresponding relationship needs to be stored in UN_LOAD_FROM_RELATIVE_CACHES_MAP
                    // That is, the cache corresponding to c needs to be updated when the current cache is updated
                    List<RelativeCache> relativeCaches = UN_LOAD_FROM_RELATIVE_CACHES_MAP.putIfAbsent(c, new ArrayList<RelativeCache>());
                    relativeCaches.add(this);
                }
            }
        }

        if (fromMappers != null && fromMappers.length > 0) {
            for (Class c : fromMappers) {
                RelativeCache relativeCache = MAPPER_CACHE_MAP.get(c);
                if (relativeCache != null) {
                    // Add the found cache to the relations hips of the current cache
                    relativeCache.addRelation(this);
                } else {
                    // If the from cache cannot be found, it proves that the from cache has not been loaded. At this time, the corresponding relationship needs to be stored in UN_LOAD_TO_RELATIVE_CACHES_MAP
                    // That is, the current cache needs to be updated when the cache corresponding to c is updated
                    List<RelativeCache> relativeCaches = UN_LOAD_TO_RELATIVE_CACHES_MAP.putIfAbsent(c, new ArrayList<RelativeCache>());
                    relativeCaches.add(this);
                }
            }
        }
    }
}
