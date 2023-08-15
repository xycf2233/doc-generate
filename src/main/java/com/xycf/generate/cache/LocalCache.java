package com.xycf.generate.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author ztc
 * @Description 本地缓存
 * @Date 2023/8/15 9:54
 */
public class LocalCache {

    private static final Map<String,Object> localCache = new HashMap<>();

    public static Object getCache(String key){
        return localCache.get(key);
    }
    public static void addCache(String key,Object value){
        localCache.put(key,value);
    }

    public static void delete(String key){
        localCache.remove(key);
    }
}
