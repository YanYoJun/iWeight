package com.plbear.iweight.view

import android.content.Context
import android.content.SharedPreferences
import android.util.SparseArray
import com.plbear.iweight.data.Data

import com.plbear.iweight.data.DataManager
import com.plbear.iweight.utils.MyLog
import com.plbear.iweight.utils.SPUtils
import com.plbear.iweight.model.settings.SettingsActivity
import com.plbear.iweight.utils.Utils

import java.util.ArrayList
import java.util.Collections
import java.util.Comparator

/**
 * Created by yanyongjun on 16/11/5.
 */

class DataAdapter(context: Context) {
    private var TAG = "DataAdapter:"
    private val mDataList = ArrayList<Data>()
    var showDataList = ArrayList<Data>()
        private set
    private var mWeightBiggest = 0f
    private var mWeightSmallest = 0f
    var timeSmallest: Long = 0
        private set
    var timeBiggest: Long = 0
        private set

    private val mListener = ArrayList<DataChangeListener>()
    private var mContext: Context? = null
    //目标体重值
    /**
     * 得到目标体重，这个体重刷新后的数据来源应用该是唯一的，就是这里
     *
     * @return
     */
    var targetWeight = -1f
        private set
    private var mDataManager: DataManager? = null
    private val DEFAULT_NUMS = 7
    private var mShowPointCount = DEFAULT_NUMS

    /**
     * 返回要展示的list的第一个id
     *
     * @return
     */
    //TODO
    var showDataStartId: Int = 0
        private set

    private var mShowAllData = false


    val showPointCount: Int
        get() = if (mShowAllData) showDataList.size else mShowPointCount

    val weightBiggest: Float
        get() = if (targetWeight == -1f) {
            trueWeightBiggest
        } else (if (mWeightBiggest > targetWeight) mWeightBiggest else targetWeight) + 5

    val weightSmallest: Float
        get() {
            return if (targetWeight == -1f) {
                trueWeightSmallest
            } else (if (mWeightSmallest < targetWeight) mWeightSmallest else targetWeight) - 5
        }

    val trueWeightBiggest: Float
        get() = mWeightBiggest + 5

    val trueWeightSmallest: Float
        get() = mWeightSmallest - 5


    val height: Float
        get() = weightBiggest - weightSmallest

    init {
        init(context)
    }

    fun setShowAllData(flag: Boolean) {
        mShowAllData = flag
        notifyDataSetChange()
    }

    fun setTag(tag: String) {
        TAG += tag
        MyLog.e(TAG, "setTag")
    }

    /**
     * init 初始化各个参数值，在构造方法中进行调用
     *
     * @returnd
     */
    private fun init(context: Context) {
        mContext = context
        val sp = SPUtils.getSp()
        targetWeight = sp.getFloat(SettingsActivity.PREFERENCE_KEY_SET_TARGET_WEIGHT, -1f) * Utils.getValueUnit()

        val db = DataManager.getInstance(context)
        val list = db!!.queryAll()
        mDataList.clear()
        mDataList.addAll(list)

        setShowList(mDataList)

        mDataManager = DataManager.getInstance(mContext)
    }

    fun setShowNum(num: Int) {
        mShowPointCount = num
        notifyDataSetChange()
    }

    interface DataChangeListener {
        fun onChange()
    }


    fun registerDataListener(listener: DataChangeListener) {
        mListener.add(listener)
    }


    /**
     * 通知注册Adapter Lisntenr的类进行刷新
     */
    private fun notifyDataChange() {
        val sp = SPUtils.getSp()
        targetWeight = sp.getFloat(SettingsActivity.PREFERENCE_KEY_SET_TARGET_WEIGHT, -1f) * Utils.getValueUnit()
        for (listener in mListener) {
            listener.onChange()
        }
    }

    fun notifyDataSetChange() {
        MyLog.e(TAG, "notifyDataSetChange")
        val db = DataManager.getInstance(mContext)
        val list = db!!.queryAll()
        mDataList.clear()
        mDataList.addAll(list)

        setShowList(list)

        notifyDataChange()
    }

    fun notifyDataPosSetChange(moveLength: Float) {
        //TODO
    }

    private fun setShowList(allList: ArrayList<Data>?) {
        if (allList == null || allList.size == 0) {
            showDataList.clear()
            mWeightBiggest = 0f
            mWeightSmallest = 0f
            timeSmallest = 0
            timeBiggest = 0
            return
        }
        MyLog.e(TAG, "setShowList:$mShowPointCount:mShowAllData:$mShowAllData")
        if (mShowAllData) {
            showDataList = allList
            showDataStartId = mDataList[0].id
            val list = resolveShowList()
            timeBiggest = java.lang.Long.valueOf(list.get(1))!!
            timeSmallest = java.lang.Long.valueOf(list.get(2))!!
            mWeightBiggest = java.lang.Float.valueOf(list.get(3))!!
            mWeightSmallest = java.lang.Float.valueOf(list.get(4))!!
            return
        }
        if (mDataList.size <= mShowPointCount) {
            showDataStartId = mDataList[0].id
        } else {
            showDataStartId = mDataList[mDataList.size - mShowPointCount.toInt()].id
        }

        showDataList.clear()
        var add = false
        var count = 0
        for (data in allList) {
            if (data.id === showDataStartId) {
                add = true
            }
            if (add) {
                count++
                if (count > mShowPointCount) {
                    break
                }
                showDataList.add(data)
            }
        }
        val list = resolveShowList()
        timeBiggest = java.lang.Long.valueOf(list.get(1))!!
        timeSmallest = java.lang.Long.valueOf(list.get(2))!!
        mWeightBiggest = java.lang.Float.valueOf(list.get(3))!!
        mWeightSmallest = java.lang.Float.valueOf(list.get(4))!!
    }

    private fun sort() {
        Collections.sort<Data>(mDataList) { data, data2 ->
            if (data.time > data2.time) {
                1
            } else {
                -1
            }
        }
    }


    fun resolveShowList(): SparseArray<String> {
        val list = SparseArray<String>()
        var timeBiggest: Long = 0
        var timeSmallest = java.lang.Long.MAX_VALUE
        var weightBiggest = 0f
        var weightSmallest = java.lang.Float.MAX_VALUE
        for (data in showDataList) {
            if (timeBiggest <= data.time) {
                timeBiggest = data.time
            }
            if (timeSmallest >= data.time) {
                timeSmallest = data.time
            }
            if (weightBiggest <= data.weight) {
                weightBiggest = data.weight
            }
            if (weightSmallest >= data.weight) {
                weightSmallest = data.weight
            }
        }
        list.put(1, timeBiggest.toString())
        list.put(2, timeSmallest.toString())
        list.put(3, weightBiggest.toString())
        list.put(4, weightSmallest.toString())
        return list
    }


}
