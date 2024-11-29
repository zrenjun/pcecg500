package com.lepu.pcecg500

import android.app.Application
import com.CommonApp.init


class JavaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        init(this)
    }
}
