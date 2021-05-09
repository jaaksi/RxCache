package org.jaaksi.rxcache.stategy

import kotlinx.coroutines.flow.Flow
import org.jaaksi.rxcache.RxCache
import org.jaaksi.rxcache.model.CacheResult
import java.lang.reflect.Type

/**
 * 缓存策略的接口
 */
interface IStrategy {
    /**
     * 根据缓存策略处理，返回对应的Flow
     *
     * @param cacheKey  缓存的key
     * @param cacheTime 缓存时间
     * @param netSource 网络请求对象
     */
    fun <T> execute(
        cache: RxCache,
        cacheKey: String,
        netSource: Flow<CacheResult<T?>>,
        type: Type
    ): Flow<CacheResult<T?>>
}