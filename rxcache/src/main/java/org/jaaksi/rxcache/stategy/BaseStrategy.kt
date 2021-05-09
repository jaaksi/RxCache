package org.jaaksi.rxcache.stategy

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jaaksi.rxcache.RxCache
import org.jaaksi.rxcache.model.CacheResult
import java.lang.reflect.Type

/**
 * Created by jaaksi on 2021/4/29.
 *
 * 缓存策略的base抽象类，提供 load data from cache and remote
 */
abstract class BaseStrategy : IStrategy {

    fun <T> loadCache(
        cache: RxCache, cacheKey: String, type: Type
    ): Flow<CacheResult<T?>> {
        return cache.rxGetInner<T?>(cacheKey, type).map {
            CacheResult(true, it)
        }
    }
}