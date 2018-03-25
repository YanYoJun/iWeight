package com.plbear.iweight.model.details

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView

import com.plbear.iweight.R
import com.plbear.iweight.data.Data
import com.plbear.iweight.data.DataManager
import com.plbear.iweight.utils.Utils

import java.util.ArrayList
import java.util.HashMap

/**
 * Created by yanyongjun on 2016/11/23.
 */

class DetailsAdapter(context: Context, list: ArrayList<Data>, callback: OnItemClick) : BaseAdapter() {
    private var mContext: Context? = null
    private var mListData: ArrayList<Data>? = null
    private var mInflater: LayoutInflater? = null
    var isEditMode = false
        private set
    private val mSelectMap = HashMap<Int, Boolean>()
    private var mSelectCount = 0
    private var mCallback: OnItemClick? = null

    val selectData: ArrayList<Data>
        get() {
            val list = ArrayList<Data>(mSelectCount)
            val it = mSelectMap.keys.iterator()
            while (it.hasNext()) {
                val key = it.next()
                if (mSelectMap[key] == true) {
                    list.add(mListData!![key])
                }
            }
            return list
        }

    interface OnItemClick {
        fun itemClick(mSelectMap: HashMap<Int, Boolean>, selectCount: Int)

        fun longItemClick(editMode: Boolean)
    }

    init {
        mContext = context
        mListData = list
        mInflater = LayoutInflater.from(context)
        mCallback = callback
    }

    override fun notifyDataSetChanged() {
        val db = DataManager.getInstance(mContext)
        mListData = db!!.queryAll()
        Utils.sortDataBigToSmall(mListData!!)
        super.notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        var view: View? = null
        if (isEditMode) {
            view = mInflater!!.inflate(R.layout.item_details_editmode, null)
        } else {
            view = mInflater!!.inflate(R.layout.item_details, null)
        }
        val labDate = view!!.findViewById<View>(R.id.lab_details_item_date) as TextView
        val labWeight = view.findViewById<View>(R.id.lab_details_item_weight) as TextView
        val data = mListData!![position]
        labDate.text = Utils.formatTimeFull(data.time)
        labWeight.setText(""+data.weight)
        if (isEditMode) {
            val selectBox = view.findViewById<View>(R.id.checkbox_details_item_select) as CheckBox
            selectBox.visibility = View.VISIBLE
            selectBox.setOnClickListener(null)
            selectBox.isClickable = false
            val isCheck = mSelectMap[position]
            selectBox.isChecked = isCheck == true
            view.setOnClickListener {
                val temp = mSelectMap[position]
                val click = temp ?: false
                mSelectMap.put(position, !click)
                if (click) {
                    mSelectCount--
                } else {
                    mSelectCount++
                }
                selectBox.isChecked = !click
                mCallback!!.itemClick(mSelectMap, mSelectCount)
            }
        } else {
            view.isClickable = false
            view.setOnClickListener(null)
            view.setOnLongClickListener {
                enterEditMode()
                true
            }
        }
        return view
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getCount(): Int {
        return mListData!!.size
    }

    fun enterEditMode() {
        if (!isEditMode) {
            isEditMode = true
            mSelectMap.clear()
            notifyDataSetChanged()
        }
        mCallback!!.longItemClick(isEditMode)
    }

    fun exitEditMode() {
        if (isEditMode) {
            isEditMode = false
            mSelectMap.clear()
            mSelectCount = 0
            notifyDataSetChanged()
        }
        mCallback!!.longItemClick(isEditMode)

    }

    /**
     * on select all click
     */
    fun onSelectAllClick() {
        val isSelectAll = mSelectCount == mListData!!.size
        if (isSelectAll) {
            mSelectMap.clear()
            mSelectCount = 0
        } else {
            for (i in mListData!!.indices) {
                mSelectMap.put(i, true)
            }
            mSelectCount = mListData!!.size
        }
        notifyDataSetChanged()
        mCallback!!.itemClick(mSelectMap, mSelectCount)
    }

    companion object {
        private val TAG = "DetailsAdapter"
    }
}
