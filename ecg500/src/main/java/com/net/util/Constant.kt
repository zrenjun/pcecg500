package com.net.util

import com.lepu.ecg500.BuildConfig


/**
 *
 *  说明: 常量
 *  zrj 2022/3/7 16:27
 *
 */
object Constant {
    var AI_BASE_URL =
        if (BuildConfig.DEBUG) "http://www.lepuaicloud.com" else "https://wisemedical.creative-sz.com" //生产服
    const val TOKEN_KEY = "TOKEN_KEY"
    const val TOKEN_EXPIRES = "TOKEN_EXPIRES"
    //todo  替换客户自己管理平台账号
    var userId = "610423198612206399"
    var password = "123456"

}