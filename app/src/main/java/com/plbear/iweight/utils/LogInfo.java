package com.plbear.iweight.utils;

import android.database.Cursor;
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
        if (Utils.DEBUG) {
            Log.i(TAG + tag_key, info);
        }
    }

    public static void i(String tag_key, Cursor cursor) {
        if (!Utils.DEBUG) {
            return;
        }
        if (cursor == null || cursor.getCount() == 0) {
            Log.e(TAG, "cursor == null or count == 0");
            return;
        }
        cursor.moveToFirst();
        int j = 0;
        do {
            int colCount = cursor.getColumnCount();
            for (int i = 0; i < colCount; i++) {
                String str = cursor.getString(i);
                Log.i(TAG, "cursor row:" + j + " col:" + i + " values:" + str);
            }
            j++;
        } while (cursor.moveToNext());
    }

    public static void printTrace(String str) {
        new Exception(str).printStackTrace();
    }

    public static void printTrace() {
        new Exception("iweight trance").printStackTrace();
    }
}
