package com.plbear.iweight.Utils;

import android.util.Log;

/**
 * Created by koakira on 16/11/6.
 */

public class ILog {
    private final static String TAG = "iWeight";

    public static void d(String tag, String msg) {
        Log.d(TAG, tag + ":" + msg);
    }

    public static void e(String tag, String msg) {
        Log.e(TAG, tag + ":" + msg);
    }

    public static void i(String tag, String msg) {
        Log.i(TAG, tag + ":" + msg);
    }
}
