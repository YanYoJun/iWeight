package com.plbear.iweight.storage;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.plbear.iweight.base.Constant;

import java.util.HashMap;

/**
 * Created by yanyongjun on 2017/7/15.
 */

public class DBProvider extends ContentProvider {
    private static final UriMatcher uriMatcher;
    private static final HashMap<String, String> projectMap;

    private ContentResolver mResolver = null;
    private DBHelper mDBHelper = null;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(Constant.PROVIDER_AUTHORITY, "items", Constant.PROVIDER_ITEM);
        uriMatcher.addURI(Constant.PROVIDER_AUTHORITY, "items_without_notify", Constant.PROVIDER_ITEMS_WITHOUT_NOTIFY);

        projectMap = new HashMap<String, String>();
        projectMap.put("_id", "_id");
        projectMap.put("time", "time");
        projectMap.put("weight", "weight");
        projectMap.put("max(time)", "max(time)");
        projectMap.put("min(time)", "min(time)");
        projectMap.put("max(weight)", "max(weight)");
        projectMap.put("min(weight)", "min(weight)");
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mResolver = context.getContentResolver();
        mDBHelper = new DBHelper(context);

        return false;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        long id = -1;
        switch (uriMatcher.match(uri)) {
            case Constant.PROVIDER_ITEM:
                id = db.insert(DBHelper.TABLE, "_id", values);
                break;
            case Constant.PROVIDER_ITEMS_WITHOUT_NOTIFY:
                id = db.insert(DBHelper.TABLE, "_id", values);
                Uri newUri = ContentUris.withAppendedId(uri, id);
                return newUri;
            default:
                throw new IllegalArgumentException("insert Error uri:" + uri);
        }
        Uri newUri = ContentUris.withAppendedId(uri, id);
        mResolver.notifyChange(newUri, null);
        mResolver.notifyChange(uri, null);
        return newUri;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
        switch (uriMatcher.match(uri)) {
            case Constant.PROVIDER_ITEM:
                sqlBuilder.setTables(DBHelper.TABLE);
                sqlBuilder.setProjectionMap(projectMap);
                break;
            default:
                throw new IllegalArgumentException("query Error uri:" + uri);
        }
        Cursor cursor = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(mResolver, uri);
        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case Constant.PROVIDER_ITEM:
                count = db.delete(DBHelper.TABLE, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("delete Error uri:" + uri);
        }
        mResolver.notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case Constant.PROVIDER_ITEM:
                count = db.update(DBHelper.TABLE, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("update Error5 uri:" + uri);
        }
        mResolver.notifyChange(uri, null);
        return count;
    }
}
