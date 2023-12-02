package com.net.remote

import com.net.remote.gson.GsonUtil
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import okhttp3.Protocol
import java.util.*


/**
 * 请求工具类
 * zrj 2020/7/16
 */

fun getOkHttpClient(): OkHttpClient {
    val builder = OkHttpClient().newBuilder()
    val locale = Locale.getDefault()
    val lang = locale.language + "-" + locale.country
    val basicParamsInterceptor = BasicParamsInterceptor.Builder()
        .addHeaderParam("Accept-Language", lang)
        .addHeaderParam("Connection", "close")
        .build()

    builder.run {
        addInterceptor(basicParamsInterceptor)
        addInterceptor(TokenInterceptor())
        addInterceptor(HttpLogInterceptor())
        connectTimeout(120, TimeUnit.SECONDS)
        readTimeout(120, TimeUnit.SECONDS)
        writeTimeout(120, TimeUnit.SECONDS)
        retryOnConnectionFailure(true) // 错误重连
        hostnameVerifier(UnSafeHostnameVerifier())
    }

    val trustAllCert = UnSafeTrustManager()
    builder.sslSocketFactory(SSL(trustAllCert), trustAllCert)
        .protocols(Collections.singletonList(Protocol.HTTP_1_1))
    return builder.build()
}

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
inline fun <reified T> createWebService(okHttpClient: OkHttpClient, url: String): T {
    val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(GsonUtil.gson))
        .build()
    return retrofit.create(T::class.java)
}

