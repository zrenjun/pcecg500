package com.net.vm

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.Carewell.OmniEcg.jni.JniTraditionalAnalysis
import com.Carewell.OmniEcg.jni.JniTraditionalAnalysis.traditionalAnalysis
import com.Carewell.OmniEcg.jni.toJson
import com.CommonApp
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.lepu.ecg500.ecg12.EcgDataManager
import com.lepu.ecg500.ecg12.LeadType
import com.lepu.ecg500.entity.EcgSettingConfigEnum
import com.lepu.ecg500.entity.MacureResultBean
import com.lepu.ecg500.entity.PatientInfoBean
import com.lepu.ecg500.util.XmlUtil
import com.net.bean.AiECG12
import com.net.bean.BatchECGRep
import com.net.bean.Patient
import com.net.remote.Repository
import com.net.remote.UrlDownload
import com.net.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class GetPDFViewModel(private val repository: Repository) : BaseViewModel() {

    val mECGPdf: MutableLiveData<String> = MutableLiveData()
    val mLocalResult: MutableLiveData<List<String>> = MutableLiveData()
    val mLocalResultBean: MutableLiveData<MacureResultBean> = MutableLiveData()

    private val dir = CommonApp.context.filesDir.absolutePath
    var isPdf = true
        set(value) {
            field = value
        }
    private var xmlPath = "" //保存xml路径
    private var defaultFilePath = "${dir}/ecg500"

    @SuppressLint("SimpleDateFormat")
    private var defaultFileName =
        SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis())

    /**
     * filePath: pdf，xml 保存路径   外置存储需要权限
     * fileName: xml文件名
     */

    fun getAIPdf(
        ecgDataArray: List<List<Short>>,
        patient: Patient,
        filePath: String?,
        fileName: String?,
        startTime: Long,
        endTime: Long
    ) {
        launch {
            withContext(Dispatchers.IO) {
                if (filePath != null) {
                    defaultFilePath = filePath
                }
                if (fileName != null) {
                    defaultFileName = fileName
                }
                val aiECG12 = AiECG12().apply {
                    idcardNo = patient.idcard
                    isAI = "1"
                    smachineCode = patient.smachineCode
                    userId = patient.userId
                    sn = "123456"  //预留字段
                    createdBy = patient.createdBy
                    createdDate = patient.createdDate
                    detectDep = "心血管"
                    detectPoint = "12导联"
                    duns = patient.duns
                }

                val data = ArrayList<ShortArray>()
                for (i in 0..11) {
                    data.add(ecgDataArray[i].toShortArray())
                }

                //1.生成心电分析xml上传
                XmlUtil.makeHl7Xml(
                    CommonApp.context,
                    "610423198612206399",
                    null,
                    data.toTypedArray(),
                    LeadType.LEAD_12,
                    defaultFilePath,
                    defaultFileName,
                    startTime,
                    endTime,
                    "35",
                    "0.67",
                    "50"
                )
                //3.获取结果id
                xmlPath = "${defaultFilePath}/${defaultFileName}.xml"
                upload(aiECG12, patient, File(xmlPath), defaultFilePath)
            }
        }
    }

    /**
     * filePath: pdf，xml 保存路径   外置存储需要权限
     * fileName: xml文件名
     */
    @SuppressLint("SimpleDateFormat")
    fun getLocalPdf(
        ecgDataArray: List<List<Short>>,
        patientInfoBean: PatientInfoBean,
        filePath: String?,
        fileName: String?,
        startTime: Long,
        endTime: Long
    ) {

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                //I/II/III/aVR/aVL/aVF/V1/V2/V3/V4/V5/V6
                val data = ArrayList<ShortArray>()
                var index: Int
                for (i in 0..7) {
                    index = i
                    if (i > 1) {
                        index = i + 4
                    }
                    data.add(ecgDataArray[index].toShortArray())
                }
                if (filePath != null) {
                    defaultFilePath = filePath
                }
                if (fileName != null) {
                    defaultFileName = fileName
                }
                XmlUtil.createDir(defaultFilePath)
                //本地算法分析，分析出来数据
                val xmlPath = "${defaultFilePath}/${defaultFileName}.xml"
                val resultBean = traditionalAnalysis(
                    xmlPath,
                    EcgSettingConfigEnum.LeadType.LEAD_12,
                    patientInfoBean,
                    data.toTypedArray()
                )
                LogUtil.e(resultBean.toJson())
                mLocalResultBean.postValue(resultBean)
                //诊断结论
                val result = mutableListOf<String>()
                resultBean.aiResultBean.aiResultDiagnosisBean.diagnosis.forEach {
                    XmlUtil.map[it.code]?.apply {
                        result.add(this)
                    }
                }
                mLocalResult.postValue(result)
                //2.生成心电分析xml  参数根据UI设置
                data.clear()
                for (i in 0..11) {
                    data.add(ecgDataArray[i].toShortArray())
                }

                XmlUtil.makeHl7Xml(
                    CommonApp.context,
                    patientInfoBean.patientNumber,
                    resultBean,
                    data.toTypedArray(),
                    LeadType.LEAD_12,
                    defaultFilePath,
                    defaultFileName,
                    startTime,
                    endTime,
                    "35",
                    "0.67",
                    "50"
                )
                //3.返回Bitmap 写文件保存或是直接展示
                val imageBitmap = EcgDataManager.instance?.exportBmp(
                    CommonApp.context,
                    patientInfoBean,
                    resultBean,
                    data.toTypedArray(),
                    startTime,
                    "35",
                    "0.67",
                    "50"
                )
                val stream = FileOutputStream(File("${defaultFilePath}/${defaultFileName}.jpg"))
                imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 25, stream)
                stream.flush()
                stream.close()
                //4.生成心电分析PDF
                imageBitmap?.let {
                   val pdfFile = EcgDataManager.instance?.exportPdf(it, "${defaultFilePath}/${defaultFileName}.pdf")
                    val file = if (isPdf) pdfFile?.absolutePath?:"" else xmlPath
                    mECGPdf.postValue(file)
                }

            }
        }
    }

    private suspend fun upload(aiECG12: AiECG12, patient: Patient, xml: File, pdfPath: String) {
        val uploadFileResult =
            repository.uploadECG(aiECG12, patient, xml)
        if (uploadFileResult.code == 0) {
            val body = BatchECGRep().apply { ids.add(uploadFileResult.data ?: "") }.toJson()
            getAIReport(body, pdfPath)
        } else {
            mECGPdf.postValue(uploadFileResult.message)
        }
    }


    private var ticker: ReceiveChannel<Unit>? = null

    //间隔5秒获取报告
    @OptIn(ObsoleteCoroutinesApi::class)
    private fun getAIReport(body: String, filePath: String) {
        ticker = ticker(5 * 1000L, 0)
        viewModelScope.launch {
            for (event in ticker!!) {
                //4.通过id获取报告url
                val repStructBatch = repository.getECGReportIds(body)
                if (repStructBatch.code == 0) {
                    val url = repStructBatch.data?.get(0)?.reportUrl ?: ""
                    if (url.isNotEmpty()) {
                        val arrStr = url.trim().split("/") //文件名后缀不能包含空格
                        val temp = arrStr[arrStr.size - 1]
                        if (UrlDownload.asyncDownload(url, temp, filePath)) {
                            val file = if (isPdf) "${filePath}/${temp}" else xmlPath
                            mECGPdf.postValue(file)
                            ticker?.cancel()
                        }
                    }
                } else {
                    mECGPdf.postValue(repStructBatch.message)
                }
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        ticker?.cancel()
    }
}


fun Any.toJson(
    dateFormat: String = "yyyy-MM-dd HH:mm:ss",
    lenient: Boolean = false,
    excludeFields: List<String>? = null
) = GsonBuilder().setDateFormat(dateFormat)
    .apply {
        if (lenient) setLenient()
        if (!excludeFields.isNullOrEmpty()) {
            setExclusionStrategies(object : ExclusionStrategy {
                override fun shouldSkipField(f: FieldAttributes?): Boolean {
                    return f != null && excludeFields.contains(f.name)
                }

                override fun shouldSkipClass(clazz: Class<*>?) = false
            })
        }
    }
    .create().toJson(this)



