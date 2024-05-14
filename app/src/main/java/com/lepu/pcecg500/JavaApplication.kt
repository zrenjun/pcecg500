package com.lepu.pcecg500

import android.app.Application
import com.CommonApp.init
import com.net.KoinInit.koinStart
import com.net.util.LogUtil

/**
 * 说明: java
 * zrj 2022/3/24 15:18
 */
class JavaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LogUtil.init(this)
        init(this)
        koinStart(this)

    }
}
