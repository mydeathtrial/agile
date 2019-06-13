package com.agile.common.util;

import net.sf.ehcache.Element;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.StandardCharsets;

/**
 * @author 佟盟
 * 日期 2019/4/19 17:35
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class CacheUtil {
    private static final String DEFAULT_CACHE_NAME = "common-cache";

    public static Cache getCache(String name) {
        return getCacheManager().getCache(name);
    }

    public static Cache getCache() {
        return getCacheManager().getCache(DEFAULT_CACHE_NAME);
    }

    public static RedisConnectionFactory getRedisConnectionFactory() {
        return FactoryUtil.getBean(RedisConnectionFactory.class);
    }

    public static CacheManager getCacheManager() {
        CacheManager cacheManager = FactoryUtil.getBean(CacheManager.class);
        if (cacheManager == null) {
            throw new RuntimeException("not fount CacheManager Instance can be use");
        }
        return cacheManager;
    }

    public static RedisTemplate<Object, Object> getRedisTemplate() {
        RedisTemplate redisTemplate = (RedisTemplate) FactoryUtil.getBean("redisTemplate");
        if (redisTemplate == null) {
            throw new RuntimeException("not fount RedisTemplate Instance can be use");
        }
        return redisTemplate;
    }

    public static Object get(Object key) {
        Cache.ValueWrapper valueWrapper = getCache().get(key);
        if (valueWrapper != null) {
            return valueWrapper.get();
        }
        return null;
    }

    public static <T> T get(Object key, Class<T> type) {
        return getCache().get(key, type);
    }

    public static void put(Object key, Object value) {
        getCache().put(key, value);
    }

    public static void put(Cache cache, Object key, Object value, int timeout) {
        CacheManager cacheManager = getCacheManager();
        if (cacheManager instanceof EhCacheCacheManager) {
            if (cache == null) {
                throw new RuntimeException("not found Cache Instance");
            }
            net.sf.ehcache.Cache currentCache = (net.sf.ehcache.Cache) cache.getNativeCache();
            Element element = new Element(key, value);
            element.setTimeToLive(timeout);
            element.setTimeToIdle(timeout);
            element.setEternal(true);
            currentCache.put(element);
        } else {
            byte[] byteKey = serializeKey(cache, key);
            byte[] byteValue = serializeValue(getRedisTemplate().getValueSerializer(), value);
            getRedisConnectionFactory().getConnection().set(byteKey, byteValue, Expiration.seconds(timeout), RedisStringCommands.SetOption.UPSERT);
        }
    }

    public static void put(Object key, Object value, int timeout) {
        CacheManager cacheManager = getCacheManager();
        put(cacheManager.getCache(DEFAULT_CACHE_NAME), key, value, timeout);
    }

    private static byte[] serializeValue(RedisSerializer redisSerializer, Object value) {
        if (redisSerializer instanceof StringRedisSerializer) {
            return redisSerializer.serialize(String.valueOf(value));
        }
        return redisSerializer.serialize(value);
    }

    public static byte[] serializeKey(Cache cache, Object key) {
        return (cache.getName() + "::" + key).getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] serializeKey(Object key) {
        return (DEFAULT_CACHE_NAME + "::" + key).getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] serializeValue(Object value) {
        return serializeValue(getRedisTemplate().getValueSerializer(), value);
    }

    public static Cache.ValueWrapper putIfAbsent(Object key, Object value) {
        return getCache().putIfAbsent(key, value);
    }

    public static void evict(Object key) {
        getCache().evict(key);
    }

    public static void clear() {
        getCache().clear();
    }

    public static boolean containKey(Object key) {
        return !ObjectUtil.isEmpty(getCache().get(key));
    }

}
