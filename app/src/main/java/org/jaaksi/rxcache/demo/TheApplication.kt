package org.jaaksi.rxcache.demo

import android.app.Application
import org.jaaksi.rxcache.RxCache
import org.jaaksi.rxcache.util.CacheLog

class TheApplication : Application() {
    companion object {
        lateinit var instance: TheApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        CacheLog.debug = BuildConfig.DEBUG
        RxCache.initialize(this)
    }
}