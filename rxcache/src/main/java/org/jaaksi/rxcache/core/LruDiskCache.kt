package org.jaaksi.rxcache.core

import com.jakewharton.disklrucache.DiskLruCache
import org.jaaksi.rxcache.converter.ICacheConverter
import org.jaaksi.rxcache.model.RealEntity
import org.jaaksi.rxcache.util.Utils
import java.io.File
import java.io.IOException
import java.lang.reflect.Type

/**
 * 磁盘缓存实现类
 */
class LruDiskCache(
    private val diskConverter: ICacheConverter,
    private val diskDir: File,
    private val appVersion: Int,
    private val diskMaxSize: Long
) : BaseCache() {

    private var mDiskLruCache: DiskLruCache? = null
    private val diskLruCache: DiskLruCache?
        get() {
            if (mDiskLruCache != null) {
                return mDiskLruCache
            }
            try {
                mDiskLruCache = DiskLruCache.open(diskDir, appVersion, 1, diskMaxSize)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return mDiskLruCache
        }

    override fun <T> doLoad(type: Type, key: String): RealEntity<T>? {
        diskLruCache ?: return null

        try {
            val edit = diskLruCache!!.edit(key)
            val source = edit.newInputStream(0)
            val value: RealEntity<T>?
            if (source != null) {
                value = diskConverter.load(source, type)
                Utils.close(source)
                edit.commit()
                return value
            }
            edit.abort()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    override fun <T> doSave(key: String, value: T): Boolean {
        diskLruCache ?: return false
        try {
            val edit = diskLruCache!!.edit(key) ?: return false
            val sink = edit.newOutputStream(0)
            if (sink != null) {
                val result = diskConverter.writer(sink, value!!)
                Utils.close(sink)
                edit.commit()
                return result
            }
            edit.abort()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    override fun doContainsKey(key: String): Boolean {
        try {
            return diskLruCache?.get(key) != null
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    override fun doRemove(key: String): Boolean {
        try {
            return diskLruCache?.remove(key) != null
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    override fun doClear(): Boolean {
        var statu = false
        try {
            diskLruCache?.delete()
            close()
            statu = true
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return statu
    }

    private fun close() {
        // close之后需要重新open，置为null，再次触发初始化
        mDiskLruCache = null
    }
}