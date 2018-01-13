package com.plbear.iweight.Data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.plbear.iweight.Utils.Constant;
import com.plbear.iweight.Utils.ILog;
import com.plbear.iweight.Utils.OldUtils;

import java.util.ArrayList;

import static android.R.attr.value;

/**
 * Created by yanyongjun on 16/11/5.
 */

public class DataManager {
    private final static String TAG = "DataManager";
    private Context mContext = null;
    private static DataManager sInstance = null;
    private ContentResolver mResolver = null;

    public static DataManager getInstance(Context context) {
        if (sInstance != null) {
            return sInstance;
        }
        if (context == null) {
            return null;
        }
        sInstance = new DataManager(context);
        return sInstance;
    }

    private DataManager(Context context) {
        mContext = context.getApplicationContext();
        mResolver = mContext.getContentResolver();
    }

    public void delete(ArrayList<Data> list) {
        if (list == null || list.size() == 0) {
            ILog.e(TAG, "list is empty");
            return;
        }
        StringBuffer strId = new StringBuffer();
        strId.append(list.get(0).getId());
        for (int i = 1; i < list.size(); i++) {
            strId.append(",");
            strId.append(list.get(i).getId());
        }
        ILog.d(TAG, "delete:" + strId.toString());
        //db.delete("weight", "_id in (" + strId.toString() + ")", null);
        mResolver.delete(Constant.CONTENT_URI, "_id in (" + strId.toString() + ")", null);
    }

    public void update(Data data) {
        if (data == null) {
            ILog.e(TAG, "update error");
            return;
        }
        ContentValues values = new ContentValues();
        values.put("weight", data.getWeight() / OldUtils.getValueUnit(mContext));
        ILog.d(TAG, "data:" + data.toString());
        mResolver.update(Constant.CONTENT_URI, values, "_id in (?)", new String[]{data.getId() + ""});
        //int value = db.update("weight", values, "_id in (?)", new String[]{data.getId() + ""});
        ILog.d(TAG, "update result:" + value);
    }

    /**
     * 插入一组数据
     *
     * @param data
     */
    public void add(Data data) {
        ILog.d(TAG, "add");
        if (data == null) {
            ILog.e(TAG, "data == null,return");
            return;
        }
        ContentValues values = new ContentValues();
        values.put("time", String.valueOf(data.getTime()));
        values.put("weight", String.valueOf(data.getWeight() / OldUtils.getValueUnit(mContext)));
        mResolver.insert(Constant.CONTENT_URI, values);
        return;
    }

    public void add(ArrayList<Data> lists) {
        if (lists == null || lists.size() == 0) {
            return;
        }
        Data lastOne = lists.get(0);
        for (Data data : lists) {
            ContentValues values = new ContentValues();
            values.put("time", String.valueOf(data.getTime()));
            values.put("weight", String.valueOf(data.getWeight() / OldUtils.getValueUnit(mContext)));
            if (lastOne == data) {
                mResolver.insert(Constant.CONTENT_URI, values);
            } else {
                mResolver.insert(Constant.CONTENT_URI_WITHOUT_NOTIRY, values);
            }
        }
        return;
    }

    public long queryLastDataTime() {
        Cursor cursor = mResolver.query(Constant.CONTENT_URI, new String[]{"max(time)"}, null, null, null);
        try {
            cursor.moveToFirst();
            long time = cursor.getLong(0);
            return time;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return -1;
    }

    public ArrayList<Data> queryAll() {
        Uri uri = Constant.CONTENT_URI;
        Cursor cursor = mResolver.query(uri, new String[]{"_id", "time", "weight"}, null, null, null);
        ArrayList<Data> list = new ArrayList<Data>();
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Data data = new Data(cursor.getInt(0), cursor.getLong(1),
                        cursor.getFloat((2)) * OldUtils.getValueUnit(mContext));
                ILog.d(TAG, "queryAll:" + data.toString());
                list.add(data);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            ILog.e(TAG, "Exception e:" + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return list;
    }
}
