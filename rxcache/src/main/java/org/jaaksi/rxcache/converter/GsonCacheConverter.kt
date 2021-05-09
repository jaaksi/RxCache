package org.jaaksi.rxcache.converter

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import org.jaaksi.rxcache.model.RealEntity
import org.jaaksi.rxcache.util.Utils
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.lang.reflect.Type

/**
 *
 * 描述：GSON-数据转换器
 * 1.GSON-数据转换器其实就是存储字符串的操作<br></br>
 * 2.如果你的Gson有特殊处理，可以自己创建一个，否则用默认。<br></br>
 *
 */
class GsonCacheConverter(private val gson: Gson) : ICacheConverter {

    override fun <T> load(source: InputStream, type: Type): RealEntity<T>? {
        var entity: RealEntity<T>? = null
        try {
            val adapter = gson.getAdapter(
                TypeToken.getParameterized(RealEntity::class.java, type)
            ) as TypeAdapter<RealEntity<T>>
            val jsonReader = gson.newJsonReader(InputStreamReader(source))
            entity = adapter.read(jsonReader)
            //value = gson.fromJson(new InputStreamReader(source), type);
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            Utils.close(source)
        }
        return entity
    }

    override fun writer(sink: OutputStream, data: Any): Boolean {
        try {
            val json = gson.toJson(data)
            val bytes = json.toByteArray()
            sink.write(bytes, 0, bytes.size)
            sink.flush()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            Utils.close(sink)
        }
        return false
    }
}