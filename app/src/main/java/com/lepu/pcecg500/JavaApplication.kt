package com.lepu.pcecg500

import android.app.Application
import com.CommonApp.init
import com.tencent.bugly.crashreport.CrashReport


class JavaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        init(this)
        CrashReport.initCrashReport(applicationContext, "39b2b79695", false)
    }
}
