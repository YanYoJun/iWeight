package com.plbear.iweight.model.details

import android.app.AlertDialog
import android.content.Context
import android.database.ContentObserver
import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.plbear.iweight.R
import com.plbear.iweight.base.Constant
import com.plbear.iweight.data.DataManager
import com.plbear.iweight.base.BaseActivity
import com.plbear.iweight.data.Data
import com.plbear.iweight.utils.MyLog
import com.plbear.iweight.utils.Utils
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.include_title.*

import java.util.ArrayList
import java.util.HashMap
import java.util.Timer
import java.util.TimerTask

/**
 * Created by yanyongjun on 2016/11/23.
 */

class DetailsActivity : BaseActivity() {

    lateinit var mAdapter: DetailsAdapter
    private var mDataList: ArrayList<Data>? = null
    private val mCallback = object : DetailsAdapter.OnItemClick {
        override fun itemClick(mSelectMap: HashMap<Int, Boolean>, selectCount: Int) {
            val size = mDataList!!.size
            if (size != selectCount) {
                btn_details_select_all.setText(R.string.select_all)
            } else {
                btn_details_select_all.setText(R.string.disselect_all)
            }
            if (selectCount == 1) {
                btn_details_change.isClickable = true
                btn_details_change.setTextColor(Color.BLACK)
            } else {
                btn_details_change.isClickable = false
                btn_details_change.setTextColor(resources.getColor(R.color.details_item_bg))
            }
            if (selectCount > 0) {
                btn_details_delete.isClickable = true
                btn_details_delete.setTextColor(Color.BLACK)
            } else {
                btn_details_delete.isClickable = false
                btn_details_delete.setTextColor(resources.getColor(R.color.details_item_bg))
            }
        }

        override fun longItemClick(editMode: Boolean) {
            if (editMode) {
                btn_details_select_all.visibility = View.VISIBLE
                btn_details_change.visibility = View.VISIBLE
                btn_details_delete.visibility = View.VISIBLE
                btn_details_select_all.setText(R.string.select_all)
                btn_details_change.isClickable = false
                btn_details_change.setTextColor(resources.getColor(R.color.details_item_bg))
                btn_details_delete.isClickable = false
                btn_details_delete.setTextColor(resources.getColor(R.color.details_item_bg))
            } else {
                btn_details_change.visibility = View.GONE
                btn_details_delete.visibility = View.GONE
            }
        }
    }

    private val mObserver = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean, uri: Uri) {
            mAdapter!!.notifyDataSetChanged()
            super.onChange(selfChange, uri)
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_details
    }

    override fun afterLayout() {
        init()
    }

    override fun onStop() {
        this.contentResolver.unregisterContentObserver(mObserver)
        super.onStop()
    }

    override fun onStart() {
        MyLog.e(TAG, "onStart1")
        this.contentResolver.registerContentObserver(Constant.CONTENT_URI, true, mObserver)
        MyLog.e(TAG, "onStart normal_2")
        super.onStart()
    }

    private fun init() {
        mDataList = DataManager.getInstance().queryAll()
        Utils.sortDataBigToSmall(mDataList!!)
        mAdapter = DetailsAdapter(this, mDataList!!, mCallback)
        listview_details.adapter = mAdapter
        listview_details.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id -> }

        //初始化标题栏
        btn_details_select_all.setOnClickListener {
            if (mAdapter.isEditMode) {
                mAdapter.onSelectAllClick()
            } else {
                mAdapter.enterEditMode()
            }
        }
        btn_details_select_all.setText("编辑")

        btn_details_change.setOnClickListener { showChangeDialog() }
        btn_details_delete.setOnClickListener { showDeleteDialog() }
    }

    fun onClick_back(v: View) {
        finish()
    }

    private fun showDeleteDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.dialog_details_delete, null)
        builder.setView(layout)
        val dialog = builder.create()
        val btnSubmit = layout.findViewById<View>(R.id.dialog_submit) as Button
        btnSubmit.setOnClickListener {
            val list = mAdapter!!.selectData
            DataManager.getInstance().delete(list)
            dialog.dismiss()
            mAdapter!!.exitEditMode()
        }

        val btnCancel = layout.findViewById<Button>(R.id.dialog_cacnel)
        btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun showChangeDialog() {
        val list = mAdapter.selectData
        MyLog.e(TAG, "yanlog list $list")
        val data = list[0]
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.dialog_main_input_weight, null)
        builder.setView(layout)
        val dialog = builder.create()
        val btnSubmit = layout.findViewById<Button>(R.id.dialog_submit)
        val editText = layout.findViewById<EditText>(R.id.dialog_input_weight)
        editText.setText("" + data.weight)
        editText.setSelection(editText.text.length)
        btnSubmit.setOnClickListener(View.OnClickListener {
            try {
                val weight = java.lang.Float.parseFloat(editText.text.toString())
                if (weight < 5 || weight > 200) {
                    Toast.makeText(this@DetailsActivity, "您输入的值太不合理了，在逗我玩吧~", Toast.LENGTH_SHORT).show()
                    return@OnClickListener
                }
                data.weight = weight
                DataManager.getInstance().update(data)
                dialog.dismiss()
                mAdapter!!.exitEditMode()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@DetailsActivity, "输入值不合法，请重新输入~", Toast.LENGTH_SHORT).show()
            }
        })

        val btnCancel = layout.findViewById<Button>(R.id.dialog_cacnel)
        btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()

        editText.requestFocus()
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (editText != null) {
                    val imm = this@DetailsActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED)
                }
            }
        }, 200)
    }

    override fun onBackPressed() {
        val adapter = listview_details.adapter as DetailsAdapter
        if (adapter.isEditMode) {
            adapter.exitEditMode()
            btn_details_select_all.setText("编辑")
            return
        }
        super.onBackPressed()
    }


}
