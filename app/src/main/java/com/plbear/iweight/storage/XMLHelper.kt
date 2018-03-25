package com.plbear.iweight.storage

import android.content.Context
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.util.Xml

import com.plbear.iweight.data.DataManager
import com.plbear.iweight.data.Data
import com.plbear.iweight.utils.MyLog
import com.plbear.iweight.utils.Utils

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlSerializer

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.ArrayList

/**
 * Created by yanyongjun on 2016/11/17.
 */

class XMLHelper(context: Context) {
    private var mDirPath: String? = null
    private val mFileName = "tizhong.xml"
    private var mContext: Context? = null
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            var listener: OnXMLListener? = null
            when (msg.what) {
                MSG_ON_READ_SUC -> {
                    listener = msg.obj as OnXMLListener
                    listener.onReadSuccess()
                }
                MSG_ON_READ_FAIL -> {
                    listener = msg.obj as OnXMLListener
                    listener.onReadFail()
                }
                MSG_ON_SAVE_FAIL -> {
                    listener = msg.obj as OnXMLListener
                    listener.onSaveFail()
                }
                MSG_ON_SAVE_SUCC -> {
                    listener = msg.obj as OnXMLListener
                    listener.onSaveSuccess()
                }
                else -> {
                }
            }
            super.handleMessage(msg)
        }
    }

    interface OnXMLListener {
        fun onReadSuccess()

        fun onSaveSuccess()

        fun onReadFail()

        fun onSaveFail()
    }

    init {
        mContext = context
        mDirPath = Environment.getExternalStorageDirectory().absolutePath + File.separator + "jilutizhong"
        MyLog.e(TAG, "savePath:" + mDirPath!!)
    }

    /**
     * save data to xml file
     * @param listen if save down
     * @return if save success ,return true,else ,return false
     */
    fun saveXML(listener: OnXMLListener): Boolean {
        MyLog.d(TAG, "saveXML")
        Thread(Runnable {
            if (saveXMLLocked()) {
                MyLog.d(TAG, "save suceesss")
                val msg = mHandler.obtainMessage(MSG_ON_SAVE_SUCC)
                msg.obj = listener
                mHandler.sendMessage(msg)
            } else {
                MyLog.d(TAG, "save fail")
                val msg = mHandler.obtainMessage(MSG_ON_SAVE_FAIL)
                msg.obj = listener
                mHandler.sendMessage(msg)
            }
        }).start()
        return true
    }

    @Synchronized private fun saveXMLLocked(): Boolean {
        val db = DataManager.getInstance(mContext)
        val list = db!!.queryAll()
        if (list == null || mDirPath == null || list.size == 0) {
            return false
        }
        val dir = File(mDirPath!!)
        MyLog.d(TAG, "makedir:" + dir.mkdirs())
        val file = File(dir, mFileName)
        try {
            if (!file.exists()) {
                file.createNewFile()
            }
            val out = FileOutputStream(file)
            return saveXMLLocked(list, out)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    @Synchronized private fun saveXMLLocked(list: ArrayList<Data>, outStream: OutputStream): Boolean {
        try {
            val serializer = Xml.newSerializer()
            serializer.setOutput(outStream, "UTF-8")
            serializer.startDocument("UTF-8", true)
            serializer.startTag(null, TAG_IWEIHT)
            serializer.startTag(null, TAG_VERSION)
            serializer.text(DBHelper.VERSION.toString())
            serializer.endTag(null, TAG_VERSION)
            for (data in list) {
                serializer.startTag(null, TAG_ITEM)
                serializer.attribute(null, ATT_DATA, data.weight.toString())
                serializer.attribute(null, ATT_TIME, data.time.toString())
                serializer.endTag(null, TAG_ITEM)
            }
            serializer.endTag(null, TAG_IWEIHT)
            serializer.endDocument()
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            try {
                outStream.flush()
                outStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return true
    }

    fun readXML(listener: OnXMLListener): Boolean {
        Thread(Runnable {
            MyLog.d(TAG, "readXML")
            val list = readXMLLocked()
            if (list == null) {
                val msg = mHandler.obtainMessage(MSG_ON_READ_FAIL)
                msg.obj = listener
                mHandler.sendMessage(msg)
                return@Runnable
            }
            val db = DataManager.getInstance(mContext)
            val listNow = db!!.queryAll()
            val listExport = ArrayList<Data>()
            if (listNow == null || listNow.size == 0) {
                db.add(list)
            } else {
                for (data in list) {
                    if (Utils.contains(listNow, data)) {
                        continue
                    }
                    listExport.add(data)
                }
                db.add(listExport)
            }
            val msg = mHandler.obtainMessage(MSG_ON_READ_SUC)
            msg.obj = listener
            mHandler.sendMessage(msg)
        }).start()
        return true
    }

    private fun readXMLLocked(): ArrayList<Data>? {
        val file = File(mDirPath, mFileName)
        if (!file.exists()) {
            return null
        }
        val list = ArrayList<Data>()
        var `in`: InputStream? = null
        try {
            `in` = FileInputStream(file)
            val pullParser = Xml.newPullParser()
            pullParser.setInput(`in`, "UTF-8")
            var event = pullParser.eventType
            while (event != XmlPullParser.END_DOCUMENT) {
                when (event) {
                    XmlPullParser.START_DOCUMENT -> {
                    }
                    XmlPullParser.START_TAG -> {
                        val name = pullParser.name
                        MyLog.d(TAG, "xmlname:" + name)
                        if (TAG_ITEM == name) {
                            val count = pullParser.attributeCount
                            val data = Data()
                            for (i in 0 until count) {
                                val attName = pullParser.getAttributeName(i)
                                if (ATT_DATA == attName) {
                                    data.weight = java.lang.Float.valueOf(pullParser.getAttributeValue(i))!!
                                } else if (ATT_TIME == attName) {
                                    data.time = java.lang.Long.valueOf(pullParser.getAttributeValue(i))!!
                                }
                            }
                            list.add(data)
                        } else if (TAG_VERSION == name) {

                        }
                    }
                    XmlPullParser.END_TAG -> {
                    }
                }
                event = pullParser.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            try {
                `in`!!.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return list
    }

    companion object {
        private val TAG_IWEIHT = "iweight"
        private val TAG_VERSION = "version"
        private val TAG_ITEM = "item"
        private val ATT_TIME = "time"
        private val ATT_DATA = "data"
        private val TAG = "XMLHelper"

        private val MSG_ON_READ_SUC = 1
        private val MSG_ON_READ_FAIL = 2
        private val MSG_ON_SAVE_FAIL = 3
        private val MSG_ON_SAVE_SUCC = 4
    }
}
