package com.Carewell.OmniEcg.jni

import android.text.TextUtils
import com.lepu.ecg500.entity.PatientInfoBean
import com.lepu.ecg500.entity.AiResultBean
import com.lepu.ecg500.entity.MacureResultBean
import com.lepu.ecg500.entity.EcgMacureResultEnum
import com.lepu.ecg500.util.Const
import com.lepu.ecg500.util.WaveEncodeUtil
import com.lepu.ecg500.entity.EcgSettingConfigEnum
import com.lepu.ecg500.entity.PatientSettingConfigEnum
import com.net.util.LogUtil
import java.lang.StringBuilder


/**
 *
 *  说明: 本地AI分析
 *  zrj 2022/2/10 9:13
 *
 */
object JniTraditionalAnalysis {

    init {
        System.loadLibrary("ecg_traditional_analysis")
    }

    /**
     * 传统算法分析
     * @param ecgMvDataArray
     * @param dataLen
     * @param aiResultBean
     * @param archivesBean
     * @param ifSaveAnalysisResult 1保存xml 结果
     * @param analysisResultPath 保存结果的path
     * @param leadMode 数据导联模式
     * @param diagnosisMode 诊断模式   //EcgDataAnalysisDiagnosisEnum   MODE_5000("5000 lib",0), MODE_CSE("CSE",1);
     */
    private external fun traditionalAnalysis(
        ecgMvDataArray: Array<FloatArray>,
        dataLen: Int,
        aiResultBean: AiResultBean,
        archivesBean: PatientInfoBean,
        ifSaveAnalysisResult: Int,
        analysisResultPath: String?,
        leadMode: Int,
        diagnosisMode: Int,
        cfgleadoff: Int
    )

    fun traditionalAnalysis(
        resultFilePath: String,
        leadType: EcgSettingConfigEnum.LeadType,
        patientInfoBean: PatientInfoBean,
        ecgDataArrayPart: Array<ShortArray?>
    ): MacureResultBean {
        //这三个字段不能为null,否则分析失败
        if (TextUtils.isEmpty(patientInfoBean.archivesName)) {
            patientInfoBean.archivesName = "w"
        }
        if (TextUtils.isEmpty(patientInfoBean.sex)) {
            patientInfoBean.sex = "0"
        }
        if (TextUtils.isEmpty(patientInfoBean.age)) {
            patientInfoBean.age = "20"
            patientInfoBean.ageUnit = PatientSettingConfigEnum.AgeUnit.YEAR
        }
        if (TextUtils.isEmpty(patientInfoBean.birthdate)) {
            patientInfoBean.birthdate = "1990-09-08"
        }
        var aiResultBean = AiResultBean()
        traditionalAnalysis(
            WaveEncodeUtil.switchEcgDataArray(ecgDataArrayPart),
            ecgDataArrayPart[0]?.size ?: 0,
            aiResultBean,
            patientInfoBean,
            1, //传统算法分析结果xml是否保存.1 保存；0 不保存
            resultFilePath,
            leadType.value,
            Const.ANALYSIS_DIAGNOSIS_MODE,
            patientInfoBean.leadoffstate
        )
        LogUtil.e(patientInfoBean.leadoffstate)
        val sb = StringBuilder()
        sb.append("JSON:")
        sb.append(aiResultBean.arrDiagnosis)
        aiResultBean.arrDiagnosis = sb.toString()
        aiResultBean = AiResultBean.manualSetValue(aiResultBean)

        //添加其它测量值 幅值，单独计算 求平均.目前不用了
        val aiResultBeanMeasuredValueList = aiResultBean.measuredValueList
        if (aiResultBeanMeasuredValueList != null && aiResultBeanMeasuredValueList.isNotEmpty()) {
            var paAvg = 0
            var qaAvg = 0
            var raAvg = 0
            var saAvg = 0
            var taAvg = 0
            var st20Avg = 0
            var st40Avg = 0
            var st60Avg = 0
            var st80Avg = 0
            var leadCount = 0
            for (i in aiResultBeanMeasuredValueList.indices) {
                if (i < 2 || i > 5) {
                    //I II V1-V6
                    val item = aiResultBeanMeasuredValueList[i]
                    paAvg += item.pa1
                    qaAvg += item.qa
                    raAvg += item.ra1
                    saAvg += item.sa1
                    taAvg += item.ta1
                    st20Avg += item.sT20
                    st40Avg += item.sT40
                    st60Avg += item.sT60
                    st80Avg += item.sT80
                    leadCount++
                }
            }
            paAvg /= leadCount
            qaAvg /= leadCount
            raAvg /= leadCount
            saAvg /= leadCount
            taAvg /= leadCount
            st20Avg /= leadCount
            st40Avg /= leadCount
            st60Avg /= leadCount
            st80Avg /= leadCount
            aiResultBean.pa = paAvg.toShort()
            aiResultBean.qa = qaAvg.toShort()
            aiResultBean.ra = raAvg.toShort()
            aiResultBean.sa = saAvg.toShort()
            aiResultBean.ta = taAvg.toShort()
            aiResultBean.sT20A = st20Avg.toShort()
            aiResultBean.sT40A = st40Avg.toShort()
            aiResultBean.sT60A = st60Avg.toShort()
            aiResultBean.sT80A = st80Avg.toShort()
        }
        val macureResultBean = MacureResultBean()
        macureResultBean.ecgMacureResultEnum = EcgMacureResultEnum.TYPE_LOCAL_TRADITIONAL_RESULT
        macureResultBean.aiResultBean = aiResultBean
        return macureResultBean
    }

}