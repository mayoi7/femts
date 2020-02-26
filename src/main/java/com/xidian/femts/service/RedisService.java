package com.xidian.femts.service;

/**
 * @author LiuHaonan
 * @date 22:08 2020/2/24
 * @email acerola.orion@foxmail.com
 */
public interface RedisService {

    /**
     * 设置缓存
     *
     * @param key   缓存的key
     * @param value 缓存的value
     */
    void set(String key, Object value);

    /**
     * 设置缓存，以及其超时时间
     *
     * @param key     缓存的key
     * @param value   缓存的value
     * @param seconds 超时时间，单位为秒
     */
    void set(String key, Object value, int seconds);

    /**
     * 获取缓存
     *
     * @param key 缓存的key
     * @return key对应的缓存，如果不存在则返回null
     */
    Object get(String key);

    /**
     * 删除缓存
     *
     * @param key 要删除的缓存的key
     */
    void del(String key);
}
