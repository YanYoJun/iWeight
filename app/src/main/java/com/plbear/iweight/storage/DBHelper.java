package com.plbear.iweight.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yanyongjun on 2018/6/11.
 */

public class DBHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "iweight.db";
    public final static int VERSION = 2;
    public final static String TABLE = "weight";
    private final static String SQL_CREATE_TABLE = "CREATE TABLE weight " + "(_id INTEGER PRIMARY KEY AUTOINCREMENT,time TEXT,weight TEXT)";
    private final static String SQL_CREATE_TABLE_2 = "create table weight " + "(_id integer primary key autoincrement,time long,weight real)";
    private final static String SQL_DELETE_TABLE = "drop table weight";


    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                Cursor cursor = db.query(TABLE, new String[]{"_id,time,weight"}, null, null, null, null, null, null);
                db.execSQL(SQL_DELETE_TABLE);
                db.execSQL(SQL_CREATE_TABLE_2);
                try {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        long time = cursor.getLong(1);
                        float weight = cursor.getFloat(2);
                        ContentValues values = new ContentValues();
                        values.put("time", time);
                        values.put("weight", weight);
                        db.insertOrThrow("weight", null, values);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
        }
    }
}
