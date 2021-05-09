package org.jaaksi.rxcache.demo.net

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private val sRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://www.wanandroid.com/")
        .client(
            OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build()
        )
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <S> create(serviceClass: Class<S>): S {
        return sRetrofit.create(serviceClass)
    }
}