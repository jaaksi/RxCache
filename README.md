一款基于kotlin协程Flow + DiskLruCache实现的磁盘缓存库，支持根据策略自动处理网络数据缓存。<br/>

该库的RxJava版[RxCache](https://github.com/jaaksi/RxJavaCache)

[toc]

## 简介
RxCache是一个本地缓存功能库，采用协程Flow + DiskLruCache来实现，线程安全内部采用ReadWriteLock机制防止频繁读写缓存造成的异常，可以独立使用，单独用RxCache来存储数据。也可以与协程结合，让你的网络库实现网络缓存功能，而且支持适用于不同业务场景的六种缓存模式。

## 关键类介绍
### RxCache
缓存核心类

### RequestApi
用于配置网络请求，写入缓存

### CacheStrategy
内部提供的6中缓存策略

## API
### 初始化
使用前必须先进行初始化操作。

```
RxCache.initialize(context)
```

也可以设置更多参数

```
/**
 * 初始化
 *
 * @param cacheDir       缓存目录
 * @param cacheVersion   缓存版本
 * @param maxCacheSize   缓存最大size
 * @param cacheConverter 缓存Converter
 */
fun initialize(
    cacheDir: File,
    cacheConverter: GsonCacheConverter = GsonCacheConverter(Gson()),
    cacheVersion: Int = 1,
    maxCacheSize: Long = MAX_CACHE_SIZE

)
```

写入数据

```
RxCache.apply {
    put("url", "111")
    put("data", BannerBean().apply {
        desc = "flutter"
        title = "flutter 中文社区"
    })
}
```

同步读取数据
```
RxCache.get("url", String::class.java)
RxCache.get("data", BannerBean::class.java)
```

异步读取数据

```
lifecycleScope.launch {
    RxCache.rxGet("url", String::class.java).collect {
        ToastUtil.toast("rxGet url = $it")
    }

    RxCache.rxGet("data", BannerBean::class.java).collect {
        ToastUtil.toast("rxGet data = ${it?.title}")
    }

}
```

移除某缓存

```
RxCache.remove("url");
```

清除全部缓存

```
lifecycleScope.launch {
    RxCache.clearAsync()
}
```

### 缓存策略
定义了IStrategy接口，框架内部提供了6中缓存策略，支持自定义。

缓存策略 | 说明
---|---
NO_CACHE | 不使用RxCache进行缓存
ONLY_REMOTE | 只请求网络，但数据依然会被缓存
ONLY_CACHE |  只加载缓存，如离线模式
FIRST_REMOTE | 优先请求网络，网络数据无效后，再加载缓存<br/>（如果缓存也没有，则会响应网络的response or error）
FIRST_CACHE | 优先加载缓存，缓存没有再去请求网络
CACHE_AND_REMOTE | 先加载缓存（成功才会回调缓存response），不管缓存什么结果都会再请求网络。<br/>如果缓存成功，网络请求数据无效，则网络不回调。<br/>如果缓存成功，网络也成功，且网络和缓存数据相同则只有缓存回调，网络不再二次回调，否则会二次回调

### 网络请求
- 生成请求的flow
- 设置缓存策略
- 设置cacheKey
- 设置cacheable，用于判断数据是否有效，有效才进行缓存
- buildCacheWithCacheResult构建
- flowOn(Dispatchers.IO)指定运行在线程中
- catch异常
- collect获取数据

```
lifecycleScope.launch {
    RequestApi(
        flow {
            emit(ApiClient.create(Api::class.java).getBanner())
        }
    )
//                .cacheStrategy(CacheStrategy.CACHE_AND_REMOTE)
        .cacheKey("banner")
        .cacheable(object : ICacheable<ApiResponse<MutableList<BannerBean>>> {
            override fun cacheable(data: ApiResponse<MutableList<BannerBean>>): Boolean {
                return data.errorCode == 0 && data.data != null
            }
        })
//                .buildCache(object : CacheType<ApiResponse<MutableList<BannerBean>>>() {})
        .buildCacheWithCacheResult(object : CacheType<ApiResponse<MutableList<BannerBean>>>() {})
        .flowOn(Dispatchers.IO)
        .catch {
            ToastUtil.toast(it.message)
            binding.textview.text = it.toString()
        }
        .collect {
            ToastUtil.toast("数据是否来自缓存：${it.isFromCache}")
            binding.textview.text = Gson().toJson(it.data)
        }
}
```

