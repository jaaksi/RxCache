package org.jaaksi.rxcache.model

import java.io.Serializable

/**
 * 缓存对象，可区分是否来自缓存
 */
class CacheResult<T> : Serializable {
    /** 是否来自缓存  */
    var isFromCache = false

    /** 用来记录，是否缓存数据（无效数据不缓存）  */
    var cacheable = false

    var data: T? = null

    constructor(isFromCache: Boolean, data: T) {
        this.isFromCache = isFromCache
        this.data = data
    }
}