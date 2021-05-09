package org.jaaksi.rxcache.util

import java.io.Closeable
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.security.MessageDigest

object Utils {

    fun md5(str: String): String {
        val md5 = try {
            MessageDigest.getInstance("MD5")
        } catch (e: Exception) {
            return str
        }

        val bs = md5.digest(str.toByteArray())
        val sb = StringBuilder(40)
        for (x in bs) {
            if ((x.toInt() and 0xff) shr 4 == 0) {
                sb.append("0").append(Integer.toHexString((x.toInt() and 0xff)))
            } else {
                sb.append(Integer.toHexString((x.toInt() and 0xff)))
            }
        }
        return sb.toString()
    }

    fun close(close: Closeable?) {
        try {
            close?.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    /**
     * 普通类反射获取泛型方式，获取需要实际解析的类型
     * 这里只考虑一个泛型参数的情况
     *
     */
    fun <T> findNeedClass(cls: Class<T>): Type {
        //以下代码是通过泛型解析实际参数,泛型必须传
        val genType = cls.genericSuperclass
        return (genType as ParameterizedType).actualTypeArguments[0]
    }
}