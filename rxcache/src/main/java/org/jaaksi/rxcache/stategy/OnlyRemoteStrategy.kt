package org.jaaksi.rxcache.stategy

import kotlinx.coroutines.flow.Flow
import org.jaaksi.rxcache.RxCache
import org.jaaksi.rxcache.model.CacheResult
import java.lang.reflect.Type

/**
 * Created by jaaksi on 2021/4/29..<br></br>
 *
 * 只请求网络，但数据依然会被缓存
 */
open class OnlyRemoteStrategy : IStrategy {
    override fun <T> execute(
        cache: RxCache,
        cacheKey: String,
        netSource: Flow<CacheResult<T?>>,
        type: Type
    ): Flow<CacheResult<T?>> {
        return netSource
    }
}