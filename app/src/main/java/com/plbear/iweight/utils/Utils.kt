package com.plbear.iweight.utils

import android.content.Context
import android.graphics.Point
import com.plbear.iweight.data.Data
import com.plbear.iweight.model.settings.SettingsActivity
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by yanyongjun on 2018/1/13.
 */
open class Utils {
    companion object {
        val TAG = "Utils"
        private var VALUE_UNIT = -1f//体重单位

        fun checkWeightValue(value: Float): Boolean {
            if (value < 2 || value > 400) {
                return false
            }
            return true
        }

        fun formatTime(time: Long): String {
            val date = Date(time)
            val format = SimpleDateFormat("MM/dd")
            return format.format(date)
        }

        fun formatTimeFull(time: Long): String {
            val date = Date(time)
            val format = SimpleDateFormat("yy/MM/dd hh:mm")
            return format.format(date)
        }

        fun contains(list: ArrayList<Data>?, data: Data?): Boolean {
            if (list == null || data == null) {
                return false
            }
            for (temp in list) {
                if (temp.equals(data)) {
                    return true
                }
            }
            return false
        }

        fun sortDataBigToSmall(list: ArrayList<Data>) {
            Collections.sort(list) { data, data2 ->
                if (data.time > data2.time) {
                    -1
                } else {
                    1
                }
            }
        }


        /**
         * 获取体重单位
         */
        fun getValueUnit(): Float {
            if (VALUE_UNIT > 0) {
                return VALUE_UNIT
            }
            try {
                val sp = SPUtils.getSp()
                val value = sp.getString(SettingsActivity.PREFERENCE_KEY_UNIT, "1")
                VALUE_UNIT = java.lang.Float.parseFloat(value)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return VALUE_UNIT
        }

        fun clearValueUnit() {
            VALUE_UNIT = -1f
        }


        /**
         * 以begin end 为起始点构造一条直线，求param点关于这条直线的对称点
         *
         * @return
         */
        fun getOppPoint(begin: Point?, end: Point?, param: Point?): Point? {
            MyLog.Companion.d(TAG, "getOppPoint:$begin:end:$end:param:$param")
            if (begin == null || end == null || param == null || begin == end || begin == param || end == param) {
                return null
            }

            val a = (1.0 * end.y - begin.y) / (end.x - begin.x)
            val c = end.y - a * end.x
            val b = -1.0
            val result = Point()
            result.x = (((b * b - a * a) * param.x - 2.0 * a * b * param.y.toDouble() - 2.0 * a * c) / (a * a + b * b)).toInt()
            result.y = (((a * a - b * b) * param.y - 2.0 * a * b * param.x.toDouble() - 2.0 * b * c) / (a * a + b * b)).toInt()
            MyLog.Companion.d(TAG, "getOppPoint result:" + result)
            return result
        }
    }
}