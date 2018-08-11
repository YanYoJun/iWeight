package com.plbear.iweight.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.plbear.iweight.base.App;

/**
 * Created by yanyongjun on 2018/4/21.
 */

public class SPUtils {
    private static final String TAG = "Sputils";
    private static SharedPreferences SP = null;

    public static SharedPreferences getSP() {
        if (SP != null) {
            return SP;
        }
        SP = PreferenceManager.getDefaultSharedPreferences(App.getAppContext());
        return SP;
    }

    public static void save(String key, String value) {
        SharedPreferences sp = getSP();
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void save(String key, Boolean value) {
        SharedPreferences sp = getSP();
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void save(String key, Float value) {
        SharedPreferences sp = getSP();
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public static void save(String key, Integer value) {
        SharedPreferences sp = getSP();
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }
}