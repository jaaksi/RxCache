package org.jaaksi.rxcache.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.jaaksi.rxcache.ICacheable
import org.jaaksi.rxcache.RequestApi
import org.jaaksi.rxcache.RxCache
import org.jaaksi.rxcache.demo.api.Api
import org.jaaksi.rxcache.demo.databinding.ActivityMainBinding
import org.jaaksi.rxcache.demo.model.ApiResponse
import org.jaaksi.rxcache.demo.model.BannerBean
import org.jaaksi.rxcache.demo.net.ApiClient
import org.jaaksi.rxcache.demo.util.ToastUtil
import org.jaaksi.rxcache.type.CacheType

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = layoutInflater.inflate(R.layout.activity_main, null)
        setContentView(view)

        binding = DataBindingUtil.bind(view)!!
        binding.apply {
            btnAdd.setOnClickListener {
                RxCache.apply {
                    put("url", "111")
                    put("data", BannerBean().apply {
                        desc = "flutter"
                        title = "flutter 中文社区"
                    })
                }
            }

            btnGet.setOnClickListener {
                lifecycleScope.launch {
                    RxCache.rxGet("url", String::class.java).collect {
                        ToastUtil.toast("rxGet url = $it")
                    }

                    RxCache.rxGet("data", BannerBean::class.java).collect {
                        ToastUtil.toast("rxGet data = ${it?.title}")
                    }

                }
            }

            btnClear.setOnClickListener {
                lifecycleScope.launch {
                    RxCache.clearAsync()
                }
            }

            btnRequest.setOnClickListener {
                request()
            }
        }
    }

    private fun request() {
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
    }
}