package org.jaaksi.rxcache.stategy

import kotlinx.coroutines.flow.Flow
import org.jaaksi.rxcache.RxCache
import org.jaaksi.rxcache.model.CacheResult
import java.lang.reflect.Type

/**
 * Created by jaaksi on 2021/4/29..<br></br>
 *
 * 只加载缓存
 */
class OnlyCacheStrategy : BaseStrategy() {

    override fun <T> execute(
        cache: RxCache,
        cacheKey: String,
        netSource: Flow<CacheResult<T?>>,
        type: Type
    ): Flow<CacheResult<T?>> {
        return loadCache(cache, cacheKey, type)
    }
}