package com.net.bean


/**
 *
 *  说明: model
 *  zrj 2022/3/7 16:24
 *
 */
class RepResultBatch {
    var reportUrl: String? = null
    var time: String? = null
    var diagnosisResult: String? = null
    var hr: String? = null  //心率
    var checkItemId: String? = null
}

class BatchECGRep {
    var ids: MutableList<String> = ArrayList()
}


class AiECG12{
    var idcardNo : String? = null//身份证号
    var duns : String? = null//机构码
    var createdDate : String? = null//检测时间
    var createdBy : String? = null//创建人
    var userId : String? = null//"610423199112269632", //创建人
    var sn : String? = null//"610423199112269632", //创建人
    var smachineCode: String? = null //机器编号
    var detectPoint : String? = null//否	检查部位
    var detectDep : String? = null//否	检查科室
    var isAI  = "0"//否	是否AI自动诊断，0否，1是
}

class Patient{
    var idcard : String? = null   //身份证号
    var duns : String? = null //机构码
    var createdDate : String? = null //检测时间
    var createdBy : String? = null//创建人
    var name : String? = null//患者姓名
    var userId : String? = null//创建人
    var sex : String? = null//患者性别
    var tel : String? = null//患者电话
    var age : String? = null//患者年龄
    var smachineCode: String? = null //机器编号
    var diagnosisMemo : String? = null //病情描述
    var birthdate :String? =null
}
