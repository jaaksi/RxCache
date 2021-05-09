package org.jaaksi.rxcache.exception

/**
 * 没有找到对应缓存时抛出异常，用于检查是否存在缓存
 */
class NoCacheException : RuntimeException("cache is null")