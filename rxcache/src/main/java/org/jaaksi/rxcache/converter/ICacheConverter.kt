package org.jaaksi.rxcache.converter

import org.jaaksi.rxcache.model.RealEntity
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.Type

/**
 * 暂时只支持GsonCacheConverter
 */
interface ICacheConverter {
    /**
     * 读取
     *
     * @param source 输入流
     * @param type 读取数据后要转换的数据类型
     * 这里没有用泛型T或者Type来做，是因为本框架决定的一些问题，泛型会丢失
     */
    fun <T> load(source: InputStream, type: Type): RealEntity<T>?

    /**
     * 写入
     *
     * @param data 保存的数据
     */
    fun writer(sink: OutputStream, data: Any): Boolean
}