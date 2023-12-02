@file:Suppress("BlockingMethodInNonBlockingContext")

package com.net.remote

import com.net.bean.AiECG12
import com.net.bean.HttpResult
import com.net.bean.Patient
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.http.Part
import java.io.File

/**
 *
 * zrj 2021/6/26 10:55
 * 更新者 2021/6/26 10:55
 */
class Repository(private val api: Api) : BaseRepository() {


    suspend fun uploadECG(ecgStr: AiECG12, patStr: Patient, file: File): HttpResult<String> {
        val fileRequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val requestFilePart =
            MultipartBody.Part.createFormData("files", file.name, fileRequestBody)
        return apiCall { api.uploadECG(ecgStr, patStr, requestFilePart) }
    }

    suspend fun getECGReportIds(ids: String) = apiCall { api.getECGReportIds(ids) }
}
