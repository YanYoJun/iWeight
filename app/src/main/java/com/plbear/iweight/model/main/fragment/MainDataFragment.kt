package com.plbear.iweight.model.main.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.plbear.iweight.R
import com.plbear.iweight.base.Constant
import com.plbear.iweight.utils.MyLog
import com.plbear.iweight.model.main.adapter.LineChartAdapter
import com.plbear.iweight.model.main.view.LineChartView

/**
 * Created by yanyongjun on 2017/7/22.
 */

class MainDataFragment : Fragment() {

    private var TAG = "MainDataFragment-" + this + "--"
    private var mView: LineChartView? = null
    private var mAdapter: LineChartAdapter? = null
    private var mShowNum = 7
    private var mShowAllData = false
    private val mHandler = Handler()
    private var mValley: TextView? = null
    private var mPeak: TextView? = null
    private var mDataChanged = false

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            MyLog.i(TAG, "action:" + action!!)
            if (ACTION_DATA_CHANED == action) {
                mDataChanged = true
            }
        }
    }


    private val mObserver = object : ContentObserver(mHandler) {
        override fun onChange(selfChange: Boolean) {
            MyLog.e(TAG, "mObserver onChange")
            mAdapter!!.notifyDataSetChange()
            mValley!!.text = (mAdapter!!.trueWeightSmallest + 5).toString()
            mPeak!!.text = (mAdapter!!.trueWeightBiggest - 5).toString()
            super.onChange(selfChange)
        }
    }

    fun setTag(tag: String) {
        TAG += tag
    }

    fun setShowDateNums(num: Int) {
        mShowNum = num
    }

    fun setShowAllData(flag: Boolean) {
        MyLog.e(TAG, "setShowAllData:" + flag)
        mShowAllData = flag
    }

    override fun onStart() {
        MyLog.i(TAG, "onStart")
        val filter = IntentFilter()
        filter.addAction(ACTION_DATA_CHANED)
        activity.registerReceiver(mReceiver, filter)
        super.onStart()
    }

    override fun onResume() {
        MyLog.i(TAG, "onResume")
        super.onResume()
        mAdapter!!.setShowNum(mShowNum)
        mAdapter!!.setShowAllData(mShowAllData)
        mValley!!.text = (mAdapter!!.trueWeightSmallest + 5).toString()
        mPeak!!.text = (mAdapter!!.trueWeightBiggest - 5).toString()
        activity.contentResolver.registerContentObserver(Constant.CONTENT_URI, true, mObserver)
        if (mDataChanged) {
            mAdapter!!.notifyDataSetChange()
            mValley!!.text = (mAdapter!!.trueWeightSmallest + 5).toString()
            mPeak!!.text = (mAdapter!!.trueWeightBiggest - 5).toString()
            mDataChanged = false
        }
    }

    override fun onPause() {
        MyLog.i(TAG, "onPause")
        activity.contentResolver.unregisterContentObserver(mObserver)
        activity.unregisterReceiver(mReceiver)
        super.onPause()
    }

    override fun onDestroy() {
        MyLog.i(TAG, "onDestroy")
        super.onDestroy()
    }

    override fun onStop() {
        MyLog.i(TAG, "onStop")

        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        MyLog.i(TAG, "onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        MyLog.i(TAG, "onCreateView")
        val v = inflater!!.inflate(R.layout.fragment_line_chart_view, null)
        mView = v.findViewById<View>(R.id.show_weight) as LineChartView
        mAdapter = mView!!.dataAdpater
        mValley = v.findViewById<View>(R.id.lab_lowest_values) as TextView
        mPeak = v.findViewById<View>(R.id.lab_highest_value) as TextView
        return v
    }

    companion object {
        val ACTION_DATA_CHANED = "com.plbear.iweight.data_changed"
    }


}
