package com.indigo.cache.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 通用缓存对象，用于统一管理缓存内容、有效期等
 *
 * @author 史偕成
 * @date 2025/05/16 09:15
 * @param <T> 缓存的数据类型
 */
@Setter
@Getter
public class CacheObject<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // Getters and Setters
    /**
     * 缓存数据
     */
    private T data;

    /**
     * 缓存创建时间
     */
    private LocalDateTime createTime;

    /**
     * 过期时间（秒），-1表示永不过期
     */
    private long expireSeconds;

    /**
     * 缓存键
     */
    private String key;

    /**
     * 版本号，用于处理缓存更新冲突
     */
    private long version;

    /**
     * 创建一个永不过期的缓存对象
     * 
     * @param key 缓存键
     * @param data 缓存数据
     */
    public CacheObject(String key, T data) {
        this(key, data, -1);
    }

    /**
     * 创建一个带有过期时间的缓存对象
     * 
     * @param key 缓存键
     * @param data 缓存数据
     * @param expireSeconds 过期时间（秒），-1表示永不过期
     */
    public CacheObject(String key, T data, long expireSeconds) {
        this.key = key;
        this.data = data;
        this.expireSeconds = expireSeconds;
        this.createTime = LocalDateTime.now();
        this.version = System.currentTimeMillis();
    }

    /**
     * 判断缓存是否过期
     * 
     * @return 是否过期
     */
    public boolean isExpired() {
        if (expireSeconds < 0) {
            return false;
        }
        
        LocalDateTime expiryTime = createTime.plusSeconds(expireSeconds);
        return LocalDateTime.now().isAfter(expiryTime);
    }

    /**
     * 获取剩余有效时间（秒）
     * 
     * @return 剩余有效时间，-1表示永不过期，0表示已过期
     */
    public long getRemainingTimeSeconds() {
        if (expireSeconds < 0) {
            return -1;
        }

        LocalDateTime expiryTime = createTime.plusSeconds(expireSeconds);
        LocalDateTime now = LocalDateTime.now();
        
        if (now.isAfter(expiryTime)) {
            return 0;
        }
        
        return java.time.Duration.between(now, expiryTime).getSeconds();
    }

    /**
     * 更新缓存数据，同时更新版本号和创建时间
     *
     * @param data 新的数据
     */
    public void updateData(T data) {
        this.data = data;
        this.createTime = LocalDateTime.now();
        this.version = System.currentTimeMillis();
    }

    /**
     * 重置过期时间，原有的过期时间会被重置
     *
     * @param expireSeconds 新的过期时间（秒）
     */
    public void resetExpiry(long expireSeconds) {
        this.expireSeconds = expireSeconds;
        this.createTime = LocalDateTime.now();
    }

    /**
     * 延长过期时间，在原有的过期时间基础上增加
     *
     * @param additionalSeconds 增加的秒数
     */
    public void extendExpiry(long additionalSeconds) {
        if (this.expireSeconds >= 0) {
            this.expireSeconds += additionalSeconds;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheObject<?> that = (CacheObject<?>) o;
        return Objects.equals(key, that.key) && version == that.version;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, version);
    }

    @Override
    public String toString() {
        return "CacheObject{" +
                "key='" + key + '\'' +
                ", createTime=" + createTime +
                ", expireSeconds=" + expireSeconds +
                ", version=" + version +
                ", isExpired=" + isExpired() +
                '}';
    }
} 