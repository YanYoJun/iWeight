package com.plbear.iweight.model.details

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast

import com.plbear.iweight.R
import com.plbear.iweight.utils.Constant
import com.plbear.iweight.data.DataManager
import com.plbear.iweight.activity.BaseActivity
import com.plbear.iweight.data.Data
import com.plbear.iweight.model.main.MainActivity
import com.plbear.iweight.utils.Utils

import java.util.ArrayList
import java.util.HashMap
import java.util.Timer
import java.util.TimerTask

/**
 * Created by yanyongjun on 2016/11/23.
 */

class DetailsActivity : BaseActivity() {
    private var mListView: ListView? = null
    private var mAdapter: DetailsAdapter? = null
    private var mDataList: ArrayList<Data>? = null
    private var mDB: DataManager? = null
    private var mBtnBack: ImageButton? = null
    private var mBtnSelectAll: Button? = null
    private var mBtnChange: Button? = null
    private var mBtnDelete: Button? = null
    private val mCallback = object : DetailsAdapter.OnItemClick {
        override fun itemClick(mSelectMap: HashMap<Int, Boolean>, selectCount: Int) {
            val size = mDataList!!.size
            if (size != selectCount) {
                mBtnSelectAll!!.setText(R.string.select_all)
            } else {
                mBtnSelectAll!!.setText(R.string.disselect_all)
            }
            if (selectCount == 1) {
                mBtnChange!!.isClickable = true
                mBtnChange!!.setTextColor(Color.BLACK)
            } else {
                mBtnChange!!.isClickable = false
                mBtnChange!!.setTextColor(resources.getColor(R.color.details_item_bg))
            }
            if (selectCount > 0) {
                mBtnDelete!!.isClickable = true
                mBtnDelete!!.setTextColor(Color.BLACK)
            } else {
                mBtnDelete!!.isClickable = false
                mBtnDelete!!.setTextColor(resources.getColor(R.color.details_item_bg))
            }
        }

        override fun longItemClick(editMode: Boolean) {
            if (editMode) {
                mBtnSelectAll!!.visibility = View.VISIBLE
                mBtnChange!!.visibility = View.VISIBLE
                mBtnDelete!!.visibility = View.VISIBLE
                mBtnSelectAll!!.setText(R.string.select_all)
                mBtnChange!!.isClickable = false
                mBtnChange!!.setTextColor(resources.getColor(R.color.details_item_bg))
                mBtnDelete!!.isClickable = false
                mBtnDelete!!.setTextColor(resources.getColor(R.color.details_item_bg))
            } else {
                mBtnSelectAll!!.visibility = View.GONE
                mBtnChange!!.visibility = View.GONE
                mBtnDelete!!.visibility = View.GONE
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
        this.contentResolver.registerContentObserver(Constant.CONTENT_URI, true, mObserver)
        super.onStart()
    }

    private fun init() {
        mBtnSelectAll = findViewById<View>(R.id.btn_details_select_all) as Button
        mListView = findViewById<View>(R.id.listview_details) as ListView
        mBtnChange = findViewById<View>(R.id.btn_details_change) as Button
        mBtnDelete = findViewById<View>(R.id.btn_details_delete) as Button
        mDB = DataManager.getInstance(this)
        mDataList = mDB!!.queryAll()
        Utils.sortDataBigToSmall(mDataList!!)
        mAdapter = DetailsAdapter(this, mDataList!!, mCallback)
        mListView!!.adapter = mAdapter
        mBtnBack = findViewById<View>(R.id.btn_back) as ImageButton
        mBtnBack!!.setOnClickListener {
            val intent = Intent(this@DetailsActivity, MainActivity::class.java)
            startActivity(intent)
        }

        mListView!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id -> }
        mBtnSelectAll!!.setOnClickListener { mAdapter!!.onSelectAllClick() }
        mBtnChange!!.setOnClickListener { showChangeDialog() }
        mBtnDelete!!.setOnClickListener { showDeleteDialog() }
    }

    private fun showDeleteDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.dialog_details_delete, findViewById<View>(R.id.dialog_layout) as ViewGroup)
        builder.setView(layout)
        val dialog = builder.create()
        val btnSubmit = layout.findViewById<View>(R.id.dialog_submit) as Button
        btnSubmit.setOnClickListener {
            val list = mAdapter!!.selectData
            mDB!!.delete(list)
            dialog.dismiss()
            mAdapter!!.exitEditMode()
        }

        val btnCancel = layout.findViewById<View>(R.id.dialog_cacnel) as Button
        btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun showChangeDialog() {
        val list = mAdapter!!.selectData
        val data = list[0]
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.dialog_main_input_weight, findViewById<View>(R.id.dialog_layout) as ViewGroup)
        builder.setView(layout)
        val dialog = builder.create()
        val btnSubmit = layout.findViewById<View>(R.id.dialog_submit) as Button
        val editText = layout.findViewById<View>(R.id.dialog_input_weight) as EditText
        editText.setText(""+data.weight)
        editText.setSelection(editText.text.length)
        btnSubmit.setOnClickListener(View.OnClickListener {
            try {
                val weight = java.lang.Float.parseFloat(editText.text.toString())
                if (weight < 5 || weight > 200) {
                    Toast.makeText(this@DetailsActivity, "您输入的值太不合理了，在逗我玩吧~", Toast.LENGTH_SHORT).show()
                    return@OnClickListener
                }
                data.weight = weight
                mDB!!.update(data)
                dialog.dismiss()
                mAdapter!!.exitEditMode()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@DetailsActivity, "输入值不合法，请重新输入~", Toast.LENGTH_SHORT).show()
            }
        })

        val btnCancel = layout.findViewById<View>(R.id.dialog_cacnel) as Button
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
        val adapter = mListView!!.adapter as DetailsAdapter
        if (adapter.isEditMode) {
            adapter.exitEditMode()
            return
        }
        super.onBackPressed()
    }


}
