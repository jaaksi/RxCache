package org.jaaksi.rxcache.stategy

import com.google.gson.Gson
import kotlinx.coroutines.flow.*
import org.jaaksi.rxcache.RxCache
import org.jaaksi.rxcache.exception.NoCacheException
import org.jaaksi.rxcache.model.CacheResult
import java.lang.reflect.Type

/**
 * Created by jaaksi on 2021/4/29.<br></br>
 *
 * 先加载缓存（成功才会回调缓存response），不管缓存什么结果都会再请求网络。<br></br>
 * 如果缓存成功，网络请求数据无效，则网络不回调<br></br>
 * 如果缓存成功，网络也成功，且网络和缓存数据相同则只有缓存回调，网络不再二次回调，否则会二次回调<br></br>
 */
open class CacheAndRemoteStrategy : BaseStrategy() {

    private val gson: Gson = Gson()

    override fun <T> execute(
        cache: RxCache,
        cacheKey: String,
        netSource: Flow<CacheResult<T?>>,
        type: Type
    ): Flow<CacheResult<T?>> {
        return loadCache<T>(cache, cacheKey, type)
            .onCompletion { cacheEx ->
                // 判断是否发生异常
                emitAll(
                    netSource.flatMapConcat { netResult ->
                        // 如果网络数据有效则正常处理
                        if (netResult.cacheable) {
                            flowOf(netResult)
                        } else {
                            // 如果网络数据是无效的，缓存也是无效的，则抛出网络的结果。如果有缓存，则网络结果不再分发
                            if (cacheEx != null) { // 没有缓存
                                flowOf(netResult)
                            } else {
                                emptyFlow()
                            }
                        }
                    }.catch { netEx ->
                        // 网络请求发生了异常，根据是否有缓存判断如何分发
                        if (cacheEx != null) { // 没有缓存，则分发网络结果
                            throw netEx
                        } else {  // 有缓存则不发射网络结果
                            emitAll(emptyFlow())
                        }
                    }
                )
            }
            .distinctUntilChanged { old, new ->
                // 如果网络数据和缓存数据一致，则只发射一次
                if (old.data == null || !new.cacheable) { // 网络无数据或没有缓存
                    false
                } else {
                    isDataSame(old.data, new.data)
                }
            }
            .catch { // 捕获NoCacheException
                if (it !is NoCacheException) {
                    throw it
                }
            }
    }

    /**
     * 子类可以重新该方法，判断网络和缓存数据是否一致（可以忽律无关紧要的数据，如时间戳）
     */
    open fun <T> isDataSame(old: T, new: T) = gson.toJson(old).hashCode() == gson.toJson(new).hashCode()
}