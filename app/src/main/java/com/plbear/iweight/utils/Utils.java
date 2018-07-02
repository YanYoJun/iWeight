package com.plbear.iweight.utils;

import android.content.SharedPreferences;
import android.graphics.Point;
import android.util.Log;
import android.widget.Toast;

import com.plbear.iweight.base.App;
import com.plbear.iweight.data.Data;
import com.plbear.iweight.model.settings.SettingsActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by yanyongjun on 2018/4/21.
 */

public class Utils {
    private final static String TAG = "Utils";
    private static float VALUE_UNIT = 1f;//体重单位

    public static final boolean DEBUG = true;

    /**
     * 获取体重单位
     */
    public static final float getValueUnit() {
        if (VALUE_UNIT > 0) {
            return VALUE_UNIT;
        }
        try {
            SharedPreferences sp = SPUtils.getSP();
            String value = sp.getString(SettingsActivity.PREFERENCE_KEY_UNIT, "1");
            VALUE_UNIT = java.lang.Float.parseFloat(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return VALUE_UNIT;
    }

    public static void clearValueUnit() {
        VALUE_UNIT = 1f;
    }

    public static boolean checkWeightValue(float value) {
        if (value < 2 || value > 400) {
            return false;
        }
        return true;
    }

    public static boolean checkWeightValueFat(float value) {
        LogInfo.e(TAG,"yanlog checkFag:"+value);
        return value <= 400;
    }

    public static String formatTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("MM/dd");
        return format.format(date);
    }

    public static String formatTimeFull(long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm");
        return format.format(date);
    }

    public static boolean contains(ArrayList<Data> list, Data data) {
        if (list == null || data == null) {
            return false;
        }
        for (Data temp : list) {
            if (temp.equals(data)) {
                return true;
            }
        }
        return false;
    }

    public static void sortDataBigToSmall(ArrayList<Data> list) {
        Collections.sort(list, new Comparator<Data>() {
            @Override
            public int compare(Data o1, Data o2) {
                if (o1.getTime() > o2.getTime()) {
                    return -1;
                }
                return 1;
            }
        });
    }

    public static void showToast(String str) {
        Toast.makeText(App.getAppContext(), str, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(int str) {
        Toast.makeText(App.getAppContext(), str, Toast.LENGTH_SHORT).show();
    }

    /**
     * 以begin end 为起始点构造一条直线，求param点关于这条直线的对称点
     *
     * @return
     */
    public static Point getOppPoint(Point begin, Point end, Point param) {
        if (begin == null || end == null || param == null || begin == end || begin == param || end == param) {
            return null;
        }

        double a = (1.0 * end.y - begin.y) / (end.x - begin.x);
        double c = end.y - a * end.x;
        double b = -1.0;
        Point result = new Point();
        result.x = (int) (((b * b - a * a) * param.x - 2.0 * a * b * param.y - 2.0 * a * c) / (a * a + b * b));
        result.y = (int) (((a * a - b * b) * param.y - 2.0 * a * b * param.x - 2.0 * b * c) / (a * a + b * b));
        return result;
    }
}
