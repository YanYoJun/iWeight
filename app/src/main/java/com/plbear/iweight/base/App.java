package com.plbear.iweight.base;

import android.app.Application;
import android.content.Context;

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
        super.onCreate();
    }
}
