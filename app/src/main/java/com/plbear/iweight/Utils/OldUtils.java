package com.plbear.iweight.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;

import com.plbear.iweight.Data.Data;
import com.plbear.iweight.model.settings.SettingsActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static android.content.ContentValues.TAG;

/**
 * Created by yanyongjun on 16/11/5.
 */

public class OldUtils {

    /**
     * Created by yanyongun on 16/11/5.
     */
    public static String formatTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("MM/dd");
        return format.format(date);
    }

    public static String formatTimeFull(long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd hh:mm");
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
            public int compare(Data data, Data data2) {
                if (data.getTime() > data2.getTime()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
    }

    private static float VALUE_UNIT = -1;//体重单位

    /**
     * 获取体重单位
     */
    public static float getValueUnit(Context context) {
        if (VALUE_UNIT > 0) {
            return VALUE_UNIT;
        }
        try {
            SharedPreferences sp = SPUtils.Companion.getSP(context);
            String value = sp.getString(SettingsActivity.PREFERENCE_KEY_UNIT, "1");
            VALUE_UNIT = Float.parseFloat(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return VALUE_UNIT;
    }
    public static void clearValueUnit(){
        VALUE_UNIT = -1;
    }


    /**
     * 以begin end 为起始点构造一条直线，求param点关于这条直线的对称点
     *
     * @return
     */
    public static Point getOppPoint(Point begin, Point end, Point param) {
        ILog.d(TAG, "getOppPoint:" + begin + ":end:" + end + ":param:" + param);
        if (begin == null || end == null || param == null || begin.equals(end) || begin.equals(param) || end.equals(param)) {
            return null;
        }

        double a = (1.0 * end.y - begin.y) / (end.x - begin.x);
        double c = end.y - a * end.x;
        double b = -1;
        Point result = new Point();
        result.x = (int) (((b * b - a * a) * param.x - 2 * a * b * param.y - 2 * a * c) / (a * a + b * b));
        result.y = (int) (((a * a - b * b) * param.y - 2 * a * b * param.x - 2 * b * c) / (a * a + b * b));
        ILog.d(TAG, "getOppPoint result:" + result);
        return result;
    }
}
