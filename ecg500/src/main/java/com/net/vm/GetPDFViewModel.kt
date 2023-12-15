package com.net.vm

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.Carewell.OmniEcg.jni.JniTraditionalAnalysis.traditionalAnalysis
import com.CommonApp
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.lepu.ecg500.entity.EcgDataInfoBean
import com.lepu.ecg500.entity.EcgSettingConfigEnum
import com.lepu.ecg500.entity.PatientInfoBean
import com.lepu.ecg500.entity.RecordSettingConfigEnum
import com.lepu.ecg500.entity.SettingsBean
import com.lepu.ecg500.util.CustomTool
import com.lepu.ecg500.util.EcgDataManager
import com.lepu.ecg500.util.XmlUtil
import com.lepu.ecg500.view.LeadType
import com.net.bean.AiECG12
import com.net.bean.BatchECGRep
import com.net.bean.Patient
import com.net.remote.Repository
import com.net.remote.UrlDownload
import com.net.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class GetPDFViewModel(private val repository: Repository) : BaseViewModel() {

    val mECGPdf: MutableLiveData<String> = MutableLiveData()
    val mLocalResult: MutableLiveData<List<String>> = MutableLiveData()

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
        ecgDataArray: List<List<Short>>?,
        setting: SettingsBean,
        patient: Patient,
        filePath: String?,
        fileName: String?
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
                //1.生成心电分析xml上传
                val xml = XmlUtil.makeHl7Xml(
                    CommonApp.context,
                    defaultFilePath,
                    defaultFileName,
                    patient.name,
                    patient.tel,
                    ecgDataArray,
                    LeadType.LEAD_12,
                    setting
                )
                if (xml.isNotEmpty()) {
                    //3.获取结果id
                    xmlPath = "${defaultFilePath}/${defaultFileName}.xml"
                    upload(aiECG12, patient, File(xmlPath), defaultFilePath)
                }
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
        fileName: String?
    ) {

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                //心电信息设置
                //基线漂移滤波 就是高通滤波.ads，baseline 都是基线漂移滤波，或者也是高通滤波
                val ecgDataInfoBean = EcgDataInfoBean()
                ecgDataInfoBean.gainArray = floatArrayOf(1f, 1f)
                ecgDataInfoBean.filterLowpass =
                    EcgSettingConfigEnum.LeadFilterType.FILTER_LOWPASS_75 //低通滤波
                ecgDataInfoBean.filterAc = EcgSettingConfigEnum.LeadFilterType.FILTER_AC_50_HZ
                ecgDataInfoBean.filterAcOpenState =
                    EcgSettingConfigEnum.LeadFilterType.FILTER_AC_OPEN
                ecgDataInfoBean.filterAds = EcgSettingConfigEnum.LeadFilterType.FILTER_BASELINE_067
                ecgDataInfoBean.leadType = EcgSettingConfigEnum.LeadType.LEAD_12 //几个导联
                ecgDataInfoBean.leadGainType = EcgSettingConfigEnum.LeadGainType.GAIN_10 //增益
                ecgDataInfoBean.leadSpeedType = EcgSettingConfigEnum.LeadSpeedType.FORMFEED_25 //走速
                val ecgDataItem = arrayOfNulls<ShortArray>(12)
                for (i in 0..11) {
                    val temp = ecgDataArray[i]
                    val shortArray = ShortArray(temp.size)
                    for (j in temp.indices) {
                        shortArray[j] = temp[j]
                    }
                    ecgDataItem[i] = shortArray
                }
                //I/II/III/aVR/aVL/aVF/V1/V2/V3/V4/V5/V6
                val data = arrayOfNulls<ShortArray>(8)
                var index = 0
                for (i in 0..7) {
                    index = i
                    if (i > 1) {
                        index = i + 4
                    }
                    val temp = ecgDataArray[index]
                    val shortArray = ShortArray(temp.size)
                    for (j in temp.indices) {
                        shortArray[j] = temp[j]
                    }
                    data[i] = shortArray
                }
                ecgDataInfoBean.ecgDataArray = ecgDataItem

                if (filePath != null) {
                    defaultFilePath = filePath
                }
                if (fileName != null) {
                    defaultFileName = fileName
                }

                XmlUtil.createDir(defaultFilePath)
                //本地算法分析，分析出来数据
                xmlPath = "${defaultFilePath}/${defaultFileName}.xml"
                val macureResultBean = traditionalAnalysis(
                    xmlPath,
                    EcgSettingConfigEnum.LeadType.LEAD_12,
                    patientInfoBean,
                    data
                )

                LogUtil.e(macureResultBean.toJson())
//                val diaResultSb = StringBuilder()
                val result = mutableListOf<String>()
                val aiResultBean = macureResultBean.aiResultBean
                val diagnosisResult = EcgDataManager.getInstance().getDiagnosisResult(aiResultBean)
                if (diagnosisResult != null && diagnosisResult.size > 0) {
                    for (i in diagnosisResult.indices) {
//                        diaResultSb.append(String.format("%d . ", i + 1))
//                        diaResultSb.append(diagnosisResult[i]).append("\t \t ")
                        result.add(diagnosisResult[i])
                    }
                }

                mLocalResult.postValue(result)
                val conf = CustomTool.getDefaultConfigBean()
                //打开该注释，只会生成1页报告，即不会生成“平均模板”，“测量矩阵”的报告。 默认是生成3页报告
                val reportSettingBeanList = conf.recordSettingBean.reportSettingBeanList
                reportSettingBeanList[RecordSettingConfigEnum.ReportSetting.AVERAGE_TEMPLATE.ordinal].isValue =
                    false //平均模板
                reportSettingBeanList[RecordSettingConfigEnum.ReportSetting.MEASURE_MATRIX.ordinal].isValue =
                    false //测量矩阵
                //生成PDF文件  ,第2页中间报告是"平均模板"，第3页报告是"测量矩阵"
                val pdfFile = EcgDataManager.getInstance().exportPdf(
                    CommonApp.context,
                    true,
                    conf,
                    patientInfoBean,
                    macureResultBean,
                    ecgDataInfoBean,
                    System.currentTimeMillis(),
                    "${defaultFilePath}/${defaultFileName}.pdf"
                )
                val file = if (isPdf) pdfFile.absolutePath else xmlPath
                mECGPdf.postValue(file)
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



