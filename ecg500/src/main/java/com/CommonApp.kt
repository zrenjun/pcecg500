package com

import android.app.Application
import android.content.Context
import kotlin.properties.Delegates


object CommonApp {
    var context: Context by Delegates.notNull()
        private set

    @JvmStatic
    fun init(applicationContext: Application) {
        context = applicationContext
    }
}