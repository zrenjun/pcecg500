package com.net.bean

import com.google.gson.annotations.SerializedName

data class HttpResult<T>(@SerializedName("data", alternate = ["result"]) val data: T?) : BaseBean()