package com.plbear.iweight.utils;

import android.util.Log;

/**
 * Created by yanyongjun on 2018/4/15.
 */

public class LogInfo {
    private static final String TAG = "iweight:";

    public static void e(String tag_key, String info) {
        Log.e(TAG + tag_key, info);
    }

    public static void e(String tag_key, int info) {
        Log.e(TAG + tag_key, info + "");
    }

    public static void i(String tag_key, String info) {
        if (MyUtils.DEBUG) {
            Log.i(TAG + tag_key, info);
        }
    }

    public static void printTrace(String str) {
        new Exception(str).printStackTrace();
    }

    public static void printTrace() {
        new Exception("iweight trance").printStackTrace();
    }
}
