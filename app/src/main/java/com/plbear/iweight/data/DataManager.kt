package com.plbear.iweight.data

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context

import com.plbear.iweight.base.Constant
import com.plbear.iweight.utils.MyLog
import com.plbear.iweight.utils.Utils

import java.util.ArrayList

import android.R.attr.value
import com.plbear.iweight.base.App
import com.plbear.iweight.utils.MyUtils

/**
 * Created by yanyongjun on 16/11/normal_5.
 */

class DataManager private constructor(context: Context) {
    private var mContext: Context? = null
    private var mResolver: ContentResolver? = null

    init {
        mContext = context.applicationContext
        mResolver = mContext!!.contentResolver
    }

    fun delete(list: ArrayList<Data>?) {
        if (list == null || list.size == 0) {
            MyLog.e(TAG, "list is empty")
            return
        }
        val strId = StringBuffer()
        strId.append(list[0].id)
        for (i in 1 until list.size) {
            strId.append(",")
            strId.append(list[i].id)
        }
        MyLog.d(TAG, "normal_delete:" + strId.toString())
        //db.normal_delete("weight", "_id in (" + strId.toString() + ")", null);
        mResolver!!.delete(Constant.CONTENT_URI, "_id in (" + strId.toString() + ")", null)
    }

    fun update(data: Data?) {
        if (data == null) {
            MyLog.e(TAG, "update error")
            return
        }
        val values = ContentValues()
        values.put("weight", data.weight / MyUtils.getValueUnit())
        MyLog.d(TAG, "data:" + data.toString())
        mResolver!!.update(Constant.CONTENT_URI, values, "_id in (?)", arrayOf(data.id.toString() + ""))
        //int value = db.update("weight", values, "_id in (?)", new String[]{data.getId() + ""});
        MyLog.d(TAG, "update result:" + value)
    }

    /**
     * 插入一组数据
     *
     * @param data
     */
    fun add(data: Data?) {
        if (data == null) {
            return
        }
        val values = ContentValues()
        values.put("time", data.time.toString())
        values.put("weight", (data.weight / MyUtils.getValueUnit()).toString())
        mResolver!!.insert(Constant.CONTENT_URI, values)
        return
    }

    fun add(lists: ArrayList<Data>?) {
        if (lists == null || lists.size == 0) {
            return
        }
        val lastOne = lists[0]
        for (data in lists) {
            val values = ContentValues()
            values.put("time", data.time.toString())
            values.put("weight", (data.weight / MyUtils.getValueUnit()).toString())
            if (lastOne == data) {
                mResolver!!.insert(Constant.CONTENT_URI, values)
            } else {
                mResolver!!.insert(Constant.CONTENT_URI_WITHOUT_NOTIRY, values)
            }
        }
        return
    }

    fun queryLastDataTime(): Long {
        val cursor = mResolver!!.query(Constant.CONTENT_URI, arrayOf("max(time)"), null, null, null)
        try {
            cursor!!.moveToFirst()
            return cursor.getLong(0)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return -1
    }

    fun queryAll(): ArrayList<Data> {
        val uri = Constant.CONTENT_URI
        val cursor = mResolver!!.query(uri, arrayOf("_id", "time", "weight"), null, null, null)
        val list = ArrayList<Data>()
        try {
            cursor!!.moveToFirst()
            while (!cursor.isAfterLast) {
                val data = Data(cursor.getInt(0), cursor.getLong(1),
                        cursor.getFloat(2) * MyUtils.getValueUnit())
                list.add(data)
                cursor.moveToNext()
            }
        } catch (e: Exception) {
        } finally {
            cursor?.close()
        }

        return list
    }

    companion object {
        private val TAG = "DataManager"
        private var sInstance: DataManager? = null

        fun getInstance(context: Context?): DataManager? {
            if (sInstance != null) {
                return sInstance
            }
            if (context == null) {
                return null
            }
            sInstance = DataManager(context)
            return sInstance
        }

        fun getInstance(): DataManager {
            if (sInstance != null) {
                return sInstance!!
            }
            sInstance = DataManager(App.getAppContext())
            return sInstance!!
        }
    }
}
