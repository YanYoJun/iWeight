package com.plbear.iweight.Utils

/**
 * Created by yanyongjun on 2018/1/13.
 */
class Utils {
    companion object {
        fun checkWeightValue(value: Float): Boolean {
            if (value < 2 || value > 400) {
                return false
            }
            return true
        }
    }
}