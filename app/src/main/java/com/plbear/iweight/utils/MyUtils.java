package com.plbear.iweight.utils;

import android.content.SharedPreferences;

import com.plbear.iweight.model.settings.SettingsActivity;

/**
 * Created by yanyongjun on 2018/4/21.
 */

public class MyUtils {
    private static float VALUE_UNIT = 1f;//体重单位

    public static final boolean DEBUG = false;

    /**
     * 获取体重单位
     */
    public static final float getValueUnit() {
        if (VALUE_UNIT > 0) {
            return VALUE_UNIT;
        }
        try {
            SharedPreferences sp = SPUtils.getSP();
            String value = sp.getString(SettingsActivity.Companion.getPREFERENCE_KEY_UNIT(), "1");
            VALUE_UNIT = java.lang.Float.parseFloat(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return VALUE_UNIT;
    }

    public static void clearValueUnit() {
        VALUE_UNIT = 1f;
    }
}
