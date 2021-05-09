package org.jaaksi.rxcache.util

import android.util.Log

object CacheLog {

    var debug = false

    fun d(tag: String, msg: String) {
        if (debug) {
            Log.d(tag, msg)
        }
    }

    fun e(tag: String, msg: String) {
        if (debug) {
            Log.e(tag, msg)
        }
    }

    fun e(tag: String, throwable: Throwable) {
        if (debug) {
            Log.e(tag, throwable.toString())
        }
    }
}