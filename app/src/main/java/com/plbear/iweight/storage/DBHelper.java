package com.plbear.iweight.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by koakira on 16/11/5.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "iweight.db";
    public static final int VERSION = 2;
    public static final String TABLE = "weight";

    private String SQL_CREATE_TABLE = "CREATE TABLE weight " +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT,time TEXT,weight TEXT)";
    private String SQL_CREATE_TABLE_2 = "create table weight " +
            "(_id integer primary key autoincrement,time long,weight real)";
    private String SQL_DELETE_TABLE = "drop table weight";

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
                        ContentValues value = new ContentValues();
                        value.put("time", time);
                        value.put("weight", weight);
                        db.insertOrThrow("weight", null, value);
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

