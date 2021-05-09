package org.jaaksi.rxcache.type

import org.jaaksi.rxcache.util.Utils.findNeedClass
import java.lang.reflect.Type

/**
 * Created by jaaksi on 2021/4/29..<br></br>
 *
 * 这里要定义为抽象类，supperclass是CacheType。获取泛型通过 cls.getGenericSuperclass()
 * new CacheType<T>() {}.getType().getClass().getGenericSuperclass();
 * 如果是非抽象类，super就是Object
</T> */
abstract class CacheType<T> {
    //获取需要解析的泛型T类型
    val type: Type
        get() = findNeedClass(javaClass)
}