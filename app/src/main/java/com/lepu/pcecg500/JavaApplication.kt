package com.lepu.pcecg500;

import android.app.Application;
import com.CommonApp;
import com.net.KoinInit;
import com.net.util.LogUtil;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * 说明: java
 * zrj 2022/3/24 15:18
 */
public class JavaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CommonApp.init(this);
        CrashReport.initCrashReport(getApplicationContext(), "39b2b79695", false);
        KoinInit.INSTANCE.koinStart(this);
        LogUtil.INSTANCE.init(this);
    }
}
