package com.plbear.iweight.storage

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by yanyongjun on 16/11/5.
 */

class DBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, VERSION) {

    private val SQL_CREATE_TABLE = "CREATE TABLE weight " + "(_id INTEGER PRIMARY KEY AUTOINCREMENT,time TEXT,weight TEXT)"
    private val SQL_CREATE_TABLE_2 = "create table weight " + "(_id integer primary key autoincrement,time long,weight real)"
    private val SQL_DELETE_TABLE = "drop table weight"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_2)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        when (oldVersion) {
            1 -> {
                val cursor = db.query(TABLE, arrayOf("_id,time,weight"), null, null, null, null, null, null)
                db.execSQL(SQL_DELETE_TABLE)
                db.execSQL(SQL_CREATE_TABLE_2)
                try {
                    cursor!!.moveToFirst()
                    while (!cursor.isAfterLast) {
                        val time = cursor.getLong(1)
                        val weight = cursor.getFloat(2)
                        val value = ContentValues()
                        value.put("time", time)
                        value.put("weight", weight)
                        db.insertOrThrow("weight", null, value)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    cursor?.close()
                }
            }
        }

    }

    companion object {
        private val DB_NAME = "iweight.db"
        val VERSION = 2
        val TABLE = "weight"
    }
}

