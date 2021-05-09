package org.jaaksi.rxcache.stategy

import kotlinx.coroutines.flow.*
import org.jaaksi.rxcache.RxCache
import org.jaaksi.rxcache.model.CacheResult
import java.lang.reflect.Type

/**
 * Created by jaaksi on 2021/4/29.<br></br>
 *
 * 优先请求网络，网络数据无效后，再加载缓存（如果缓存也没有，则会响应网络的response or error）
 */
class FirstRemoteStrategy : BaseStrategy() {
    override fun <T> execute(
        cache: RxCache,
        cacheKey: String,
        netSource: Flow<CacheResult<T?>>,
        type: Type
    ): Flow<CacheResult<T?>> {
        val cache = loadCache<T>(cache, cacheKey, type)
        return netSource.flatMapConcat { netResult ->
            // 如果网络数据有效则正常处理
            if (netResult.cacheable) {
                flowOf(netResult)
            } else {
                // 如果网络数据是无效的，则走缓存。如果被catch住，说明缓存不在，则分发网络的结果
                cache.catch {
                    emit(netResult)
                }
            }
        }.catch { ex ->
            emitAll(cache.catch {
                // 如果缓存也没有，则响应网络的结果
                throw  ex
            })
        }
    }
}