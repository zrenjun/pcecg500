package com.net.remote

import com.net.bean.HttpResult


/**
 * 统一数据获取
 * zrj 2019/7/26
 */
open class BaseRepository {
    suspend fun <T : Any> apiCall(call: suspend () -> HttpResult<T>): HttpResult<T> {
        return call.invoke()
    }
}