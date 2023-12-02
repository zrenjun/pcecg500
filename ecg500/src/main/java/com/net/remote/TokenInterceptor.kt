package com.net.remote

import android.annotation.SuppressLint
import com.net.util.Constant
import com.net.util.LogUtil
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset
import java.text.ParsePosition
import java.text.SimpleDateFormat


/**
 *
 *  说明: 客户令牌
 *  zrj 2022/3/31 11:43
 *
 */
class TokenInterceptor : Interceptor {

    private var token: String by Preference(Constant.TOKEN_KEY, "")
    private var tokenExpires: Long by Preference(Constant.TOKEN_EXPIRES, 0)

    //判断是否过期
    private fun tokenIsExpire() = System.currentTimeMillis() - tokenExpires >= 0  //30分钟

    @Suppress("DEPRECATION")
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val originalResponse = chain.proceed(request)
        val responseBody = originalResponse.body
        val source = responseBody?.source()
        source?.request(Long.MAX_VALUE)// Buffer the entire body.
        val buffer = source?.buffer()
        var charset = Charset.forName("utf8")
        val contentType = responseBody?.contentType()
        if (contentType != null) {
            charset = contentType.charset(charset)
        }
        val bodyString = buffer?.clone()?.readString(charset) ?: ""
        LogUtil.e(bodyString)
        if (bodyString.contains("302") || tokenIsExpire() || token.isEmpty()) {
            //同步请求方式，获取最新的Token
            val newToken = getNewToken()
            //使用新的Token，创建新的请求
            if (newToken.isNotEmpty()) {
                var newRequest = chain.request().newBuilder()
                val oldUrl = request.url
                val newHttpUrl = oldUrl.newBuilder()
                    .scheme(oldUrl.scheme)
                    .host(oldUrl.host)
                    .port(oldUrl.port)
                    .build()
                newRequest = newRequest.url(newHttpUrl).header("token", newToken)
                //重新请求上次的接口
                return chain.proceed(newRequest.build())
            }
        }
        return originalResponse
    }

    @SuppressLint("NewApi", "SimpleDateFormat")
    private fun getNewToken(): String {
        /**
         * 必须使用同步请求
         */
        val refreshUrl = Constant.AI_BASE_URL + "/api/v1/pc700/token"
        LogUtil.e("重新请求---$refreshUrl")
        try {
            val response = OkHttpClient().newCall(
                Request.Builder()
                    .url(refreshUrl)
                    .post(
                        FormBody.Builder()
                            .add("userId", "421023198902223431") //todo  替换客户自己管理平台账号
                            .add("password", "zrj19890222")
                            .build()
                    ).build()
            ).execute().body?.string()
            LogUtil.e("重新请求---$response")
            val objects = JSONObject(response ?: "")
            val code = objects.getString("code")
            if (code == "0") {
                val data = objects.getJSONObject("data")
                token = data.getString("token")
                val date = data.getString("expire_time")
                tokenExpires =
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date, ParsePosition(0)).time
                return token
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }
}
