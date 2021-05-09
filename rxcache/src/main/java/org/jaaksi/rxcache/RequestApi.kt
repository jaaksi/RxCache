package org.jaaksi.rxcache

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.jaaksi.rxcache.model.CacheResult
import org.jaaksi.rxcache.stategy.CacheStrategy
import org.jaaksi.rxcache.stategy.IStrategy
import org.jaaksi.rxcache.stategy.NoStrategy
import org.jaaksi.rxcache.type.CacheType
import java.lang.reflect.Type

/**
 * Created by jaaksi on 2021/4/29.
 * 用于发送网络请求
 */
class RequestApi<T> constructor(private val api: Flow<T?>) {
    private val rxCache = RxCache
    private var cacheTime: Long = RxCache.NEVER_EXPIRE
    private var cacheStrategy: IStrategy = CacheStrategy.CACHE_AND_REMOTE

    private lateinit var cacheKey: String
    private var cacheable: ICacheable<T>? = null

    /**
     * 设置缓存key
     */
    fun cacheKey(cacheKey: String): RequestApi<T> {
        this.cacheKey = cacheKey
        return this
    }

    /**
     * 校验response的有效性，数据有效才进行缓存
     */
    fun cacheable(cacheable: ICacheable<T>): RequestApi<T> {
        this.cacheable = cacheable
        return this
    }

    /**
     * 设置缓存时间
     */
    fun cacheTime(cacheTime: Long): RequestApi<T> {
        check(cacheTime == RxCache.NEVER_EXPIRE || cacheTime > 0)
        this.cacheTime = cacheTime
        return this
    }

    /**
     * 设置缓存策略
     */
    fun cacheStrategy(iStrategy: IStrategy): RequestApi<T> {
        cacheStrategy = iStrategy
        return this
    }

//    fun buildCache(type: Type): Flow<T?> {
//        return doBuildCache(type)
//    }

    /**
     * buildCache
     */
    fun buildCache(cacheType: CacheType<T>): Flow<T?> {
        return doBuildCache(cacheType.type)
    }

    private fun doBuildCache(type: Type): Flow<T?> {
        return doBuildCacheWithCacheResult(type).map {
            it.data
        }
    }

//    fun buildCacheWithCacheResult(type: Type): Flow<CacheResult<T?>> {
//        return doBuildCacheWithCacheResult(type)
//    }

    /**
     * buildCacheWithCacheResult
     */
    fun buildCacheWithCacheResult(cacheType: CacheType<T>): Flow<CacheResult<T?>> {
        return doBuildCacheWithCacheResult(cacheType.type)
    }

    // 根据不同的策略处理
    private fun doBuildCacheWithCacheResult(type: Type): Flow<CacheResult<T?>> {
        return api.map {
            val result = CacheResult(false, it)
            if (cacheStrategy !is NoStrategy) { // 如果缓存策略是不缓存
                //  这里根据业务情况处理cacheable == null的情况，提供默认的判断，如result.errno==0&&result.data!=null
                if (it != null && cacheable?.cacheable(it) != false) {
                    result.cacheable = true
                    writeCache(it)
                }
            }
            result
        }.let {
            cacheStrategy.execute(rxCache, cacheKey, it, type)
        }
    }

    private fun writeCache(t: T) {
        GlobalScope.launch {
            rxCache.rxPut(cacheKey, t)
                .retry(1) // 写入失败重试一次
                .flowOn(Dispatchers.IO)
                .collect()
        }
    }
}

interface ICacheable<T> {
    /**
     * @param data data nonNull
     * @return 是否缓存该条数据
     */
    fun cacheable(data: T): Boolean
}