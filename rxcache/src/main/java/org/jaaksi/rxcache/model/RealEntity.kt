package org.jaaksi.rxcache.model

import java.io.Serializable

/**
 * 实际缓存的类，将传入的data包裹在此类下，用以设置缓存时长等
 */
data class RealEntity<T>(
    val data: T,
    /** 缓存有效的时间，以ms为单位  */
    val duration: Long
) : Serializable {
    /** 缓存创建的时间  */
    val createTime: Long = System.currentTimeMillis()
}