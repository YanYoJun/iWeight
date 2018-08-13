package com.plbear.iweight.base;

import android.app.Application;
import android.content.Context;
import android.net.Network;

import com.plbear.iweight.data.NetworkDataManager;
import com.plbear.iweight.utils.Utils;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by yanyongjun on 2018/6/11.
 */

public class App extends Application {
    private static Context mContext;

    public static Context getAppContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        CrashReport.initCrashReport(getApplicationContext(),Constant.BUGLY_ID, Utils.DEBUG);
        NetworkDataManager.getsInstance().sync();
        super.onCreate();
    }
}
