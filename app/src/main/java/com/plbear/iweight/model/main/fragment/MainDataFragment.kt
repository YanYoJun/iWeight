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
import com.plbear.iweight.model.main.adapter.LineChartAdapter
import com.plbear.iweight.model.main.view.LineChartView

/**
 * Created by yanyongjun on 2017/normal_7/22.
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
            if (ACTION_DATA_CHANED == action) {
                mDataChanged = true
            }
        }
    }


    private val mObserver = object : ContentObserver(mHandler) {
        override fun onChange(selfChange: Boolean) {
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
        mShowAllData = flag
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater!!.inflate(R.layout.fragment_main_data, null)
        mView = v.findViewById<View>(R.id.show_weight) as LineChartView
        mAdapter = mView!!.dataAdpater
        mValley = v.findViewById<View>(R.id.lab_lowest_values) as TextView
        mPeak = v.findViewById<View>(R.id.lab_highest_value) as TextView
        return v
    }

    override fun onStart() {
        val filter = IntentFilter()
        filter.addAction(ACTION_DATA_CHANED)
        activity.registerReceiver(mReceiver, filter)
        super.onStart()
    }


    override fun onResume() {
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
        activity.contentResolver.unregisterContentObserver(mObserver)
        super.onPause()
    }

    override fun onStop() {
        activity.unregisterReceiver(mReceiver)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        val ACTION_DATA_CHANED = "com.plbear.iweight.data_changed"
    }

}
