package org.jaaksi.rxcache.core

import org.jaaksi.rxcache.model.RealEntity
import java.lang.reflect.Type
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

/**
 *
 * 描述：缓存的基类
 * 1.所有缓存处理都继承该基类<br></br>
 * 2.增加了锁机制，防止频繁读取缓存造成的异常。<br></br>
 * 3.子类直接考虑具体的实现细节就可以了。<br></br>
 */
abstract class BaseCache {
    private val mLock: ReadWriteLock = ReentrantReadWriteLock()

    /**
     * 读取缓存
     *
     * @param key 缓存key
     */
    fun <T> load(type: Type, key: String): RealEntity<T>? {
        if (!containsKey(key)) {
            return null
        }

        mLock.readLock().lock()
        return try {
            // 读取缓存
            doLoad(type, key)
        } finally {
            mLock.readLock().unlock()
        }
    }

    /**
     * 保存缓存
     *
     * @param key 缓存key
     * @param value 缓存内容
     */
    fun <T> save(key: String, value: T?): Boolean {
        //2.如果要保存的值为空,则删除
        if (value == null) {
            return remove(key)
        }

        //3.写入缓存
        var status = false
        mLock.writeLock().lock()
        status = try {
            doSave(key, value)
        } finally {
            mLock.writeLock().unlock()
        }
        return status
    }

    /**
     * 删除缓存
     */
    fun remove(key: String): Boolean {
        mLock.writeLock().lock()
        return try {
            doRemove(key)
        } finally {
            mLock.writeLock().unlock()
        }
    }

    /**
     * 清空缓存
     */
    fun clear(): Boolean {
        mLock.writeLock().lock()
        return try {
            doClear()
        } finally {
            mLock.writeLock().unlock()
        }
    }

    /**
     * 是否包含 加final 是让子类不能被重写，只能使用doContainsKey<br></br>
     * 这里加了锁处理，操作安全。<br></br>
     *
     * @param key 缓存key
     * @return 是否有缓存
     */
    fun containsKey(key: String): Boolean {
        mLock.readLock().lock()
        return try {
            doContainsKey(key)
        } finally {
            mLock.readLock().unlock()
        }
    }

    protected abstract fun doContainsKey(key: String): Boolean

    /**
     * 读取缓存
     */
    protected abstract fun <T> doLoad(type: Type, key: String): RealEntity<T>?

    /**
     * 保存
     */
    protected abstract fun <T> doSave(key: String, value: T): Boolean

    /**
     * 删除缓存
     */
    protected abstract fun doRemove(key: String): Boolean

    /**
     * 清空缓存
     */
    protected abstract fun doClear(): Boolean
}