package com.plbear.iweight.storage

import android.content.ContentProvider
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri

import com.plbear.iweight.base.Constant
import com.plbear.iweight.utils.MyLog

import java.util.HashMap

/**
 * Created by yanyongjun on 2017/7/15.
 */

class DBProvider : ContentProvider() {

    private var mResolver: ContentResolver? = null
    private var mDBHelper: DBHelper? = null

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun onCreate(): Boolean {
        val context = context
        mResolver = context!!.contentResolver
        mDBHelper = DBHelper(context)

        return false
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = mDBHelper!!.writableDatabase
        var id: Long = -1
        when (uriMatcher.match(uri)) {
            Constant.PROVIDER_ITEM -> id = db.insert(DBHelper.TABLE, "_id", values)
            Constant.PROVIDER_ITEMS_WITHOUT_NOTIFY -> {
                id = db.insert(DBHelper.TABLE, "_id", values)
                return ContentUris.withAppendedId(uri, id)
            }
            else -> throw IllegalArgumentException("insert Error uri:" + uri)
        }
        val newUri = ContentUris.withAppendedId(uri, id)
        mResolver!!.notifyChange(newUri, null)
        mResolver!!.notifyChange(uri, null)
        return newUri
    }


    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        MyLog.i(TAG,"query in")
        val db = mDBHelper!!.readableDatabase
        val sqlBuilder = SQLiteQueryBuilder()
        val item = uriMatcher.match(uri)
        when (item) {
            Constant.PROVIDER_ITEM -> {
                sqlBuilder.tables = DBHelper.TABLE
                sqlBuilder.setProjectionMap(projectMap)
            }
            else -> throw IllegalArgumentException("query Error uri:" + uri)
        }
        val cursor = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder)
        cursor.setNotificationUri(mResolver, uri)
        return cursor
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val db = mDBHelper!!.writableDatabase
        var count = 0
        when (uriMatcher.match(uri)) {
            Constant.PROVIDER_ITEM -> count = db.delete(DBHelper.TABLE, selection, selectionArgs)
            else -> throw IllegalArgumentException("delete Error uri:" + uri)
        }
        mResolver!!.notifyChange(uri, null)
        return count
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        val db = mDBHelper!!.writableDatabase
        var count = 0
        when (uriMatcher.match(uri)) {
            Constant.PROVIDER_ITEM -> count = db.update(DBHelper.TABLE, values, selection, selectionArgs)
            else -> throw IllegalArgumentException("update Error5 uri:" + uri)
        }
        mResolver!!.notifyChange(uri, null)
        return count
    }

    companion object {
        private val uriMatcher: UriMatcher
        private val projectMap: HashMap<String, String>
        private val TAG = "DBProvider"

        init {
            MyLog.e(TAG,"DB Provider init")
            uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
            uriMatcher.addURI(Constant.PROVIDER_AUTHORITY, "items", Constant.PROVIDER_ITEM)
            uriMatcher.addURI(Constant.PROVIDER_AUTHORITY, "items_without_notify", Constant.PROVIDER_ITEMS_WITHOUT_NOTIFY)

            projectMap = HashMap()
            projectMap.put("_id", "_id")
            projectMap.put("time", "time")
            projectMap.put("weight", "weight")
            projectMap.put("max(time)", "max(time)")
            projectMap.put("min(time)", "min(time)")
            projectMap.put("max(weight)", "max(weight)")
            projectMap.put("min(weight)", "min(weight)")
        }
    }
}
