package org.jaaksi.rxcache.demo.api

import org.jaaksi.rxcache.demo.model.ApiResponse
import org.jaaksi.rxcache.demo.model.BannerBean
import retrofit2.http.GET

interface Api {
    @GET("banner/json")
    suspend fun getBanner(): ApiResponse<MutableList<BannerBean>>
}