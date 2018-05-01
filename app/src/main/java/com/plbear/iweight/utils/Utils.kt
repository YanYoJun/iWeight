package com.plbear.iweight.utils

import android.graphics.Point
import android.widget.Toast
import com.plbear.iweight.base.App
import com.plbear.iweight.data.Data
import com.plbear.iweight.model.settings.SettingsActivity
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by yanyongjun on 2018/normal_1/13.
 */
open class Utils {
    companion object {
        val TAG = "Utils"

        fun checkWeightValue(value: Float): Boolean {
            if (value < 2 || value > 400) {
                return false
            }
            return true
        }

        fun checkWeightValueFat(value: Float): Boolean {
            return value <= 400
        }

        fun formatTime(time: Long): String {
            val date = Date(time)
            val format = SimpleDateFormat("MM/dd")
            return format.format(date)
        }

        fun formatTimeFull(time: Long): String {
            val date = Date(time)
            val format = SimpleDateFormat("yy/MM/dd HH:mm")
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
         * 以begin end 为起始点构造一条直线，求param点关于这条直线的对称点
         *
         * @return
         */
        fun getOppPoint(begin: Point?, end: Point?, param: Point?): Point? {
            if (begin == null || end == null || param == null || begin == end || begin == param || end == param) {
                return null
            }

            val a = (1.0 * end.y - begin.y) / (end.x - begin.x)
            val c = end.y - a * end.x
            val b = -1.0
            val result = Point()
            result.x = (((b * b - a * a) * param.x - 2.0 * a * b * param.y.toDouble() - 2.0 * a * c) / (a * a + b * b)).toInt()
            result.y = (((a * a - b * b) * param.y - 2.0 * a * b * param.x.toDouble() - 2.0 * b * c) / (a * a + b * b)).toInt()
            return result
        }

        fun showToast(str: String) {
            Toast.makeText(App.getAppContext(), str, Toast.LENGTH_SHORT).show()
        }

        fun showToast(str: Int) {
            Toast.makeText(App.getAppContext(), str, Toast.LENGTH_SHORT).show()
        }
    }
}