package com.xycf.generate.util;

import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author xycf
 */
@Component
@SuppressWarnings(value = {"unchecked", "rawtypes"})
public class RedisUtils {

    @Resource(name = "JsonRedisTemplate")
    private RedisTemplate redisTemplate;

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key      Redis键
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     */
    public <T> void setCacheObject(final String key, final T value, final Long timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key      Redis键
     * @param value    缓存的值
     */
    public <T> void setCacheObject(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value, 6L, TimeUnit.HOURS);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return true=设置成功；false=设置失败
     */
    public Boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获取有效时间
     *
     * @param key Redis键
     * @return 有效时间
     */
    public Long getExpire(final String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 判断 key是否存在
     *
     * @param key Redis键
     * @return true 存在 false不存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 获得缓存的基本对象
     *
     * @param key Redis键
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String key) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    /**
     * 批量获得缓存的基本对象
     *
     * @param keys Redis键
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getMultiCacheObject(final Collection<String> keys) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.multiGet(keys);
    }

    /**
     * 缓存List数据
     *
     * @param key      Redis键
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public <T> long setCacheList(final String key, final List<T> dataList) {
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        redisTemplate.expire(key,1L,TimeUnit.HOURS);
        return count == null ? 0 : count;
    }

    /**
     * 获得缓存的list对象
     *
     * @param key Redis键
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheList(final String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 缓存Set
     *
     * @param key     Redis键
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> dataSet) {
        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        for (T t : dataSet) {
            setOperation.add(t);
        }
        return setOperation;
    }

    /**
     * 获得缓存的set
     *
     * @param key Redis键
     * @return 集合
     */
    public <T> Set<T> getCacheSet(final String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 缓存Map
     *
     * @param key     Redis键
     * @param dataMap map
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
            redisTemplate.expire(key,1L,TimeUnit.HOURS);
        }
    }

    /**
     * 获得缓存的Map
     *
     * @param key Redis键
     * @return 哈希
     */
    public <T> Map<String, T> getCacheMap(final String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 往Hash中存入数据
     *
     * @param key   Redis键
     * @param hKey  Hash键
     * @param value 值
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value) {
        redisTemplate.opsForHash().put(key, hKey, value);
        redisTemplate.expire(key,1L,TimeUnit.HOURS);
    }

    /**
     * 获取Hash中的数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public <T> T getCacheMapValue(final String key, final String hKey) {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hKey);
    }

    /**
     * 获取多个Hash中的数据
     *
     * @param key   Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<String> hKeys) {
        return redisTemplate.opsForHash().multiGet(key, hKeys);
    }

    /**
     * 删除单个对象
     *
     * @param key Redis键
     * @param hKey Hash键
     * @return true=删除成功；false=删除失败
     */
    public Long deleteCacheMapHKey(final String key, final String hKey) {
        return redisTemplate.opsForHash().delete(key,hKey);
    }

    /**
     * 删除某个缓存所有数据
     *
     * @param key Redis键
     * @return true=删除成功；false=删除失败
     */
    public Boolean deleteCacheMap(final String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Collection<String> keys(final String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 如果存在则不插入  如果不存在则插入
     * @param key redis键
     * @param value redis 值
     * @return
     */
    public Boolean setIfAbsent(final String key,final String value) {
        Boolean absent = redisTemplate.opsForValue().setIfAbsent(key, value);
        redisTemplate.expire(key,1L,TimeUnit.HOURS);
        return absent;
    }

    /**
     * 如果存在则不插入  如果不存在则插入
     * @param key redis键
     * @param value redis 值
     * @param time 存活时间
     * @param timeUnit 时间单位
     * @return
     */
    public Boolean setIfAbsentAndTimeOut(final String key,final String value,Long time,TimeUnit timeUnit) {
        return redisTemplate.opsForValue().setIfAbsent(key,value,time,timeUnit);
    }

    /**
     * 删除缓存
     * @param key redis键
     * @return
     */
    public void deleteCache(final String key) {
        redisTemplate.delete(key);
    }

}