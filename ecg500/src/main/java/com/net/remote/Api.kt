package com.net.remote

import com.net.bean.AiECG12
import com.net.bean.HttpResult
import com.net.bean.Patient
import com.net.bean.RepResultBatch
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 *
 * java类作用描述
 * zrj 2021/8/11 10:58
 * 更新者 2021/8/11 10:58
 */
interface Api {
    /**
     * 上传12导数据
     */
    @Multipart
    @POST("/api/v1/pcecg/ecg500")
    suspend fun uploadECG(
        @Part("ecgStr") ecgStr: AiECG12,
        @Part("patStr") patStr: Patient,
        @Part file: MultipartBody.Part
    ): HttpResult<String>

    /**
     * 获取12导联心电报告pdf 批量获取
     */
    @FormUrlEncoded
    @POST("/api/v1/pcecg/list")
    suspend fun getECGReportIds(@Field("ids") ids: String): HttpResult<List<RepResultBatch>>
}