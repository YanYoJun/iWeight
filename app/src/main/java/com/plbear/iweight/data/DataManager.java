package com.plbear.iweight.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.plbear.iweight.base.App;
import com.plbear.iweight.base.Constant;
import com.plbear.iweight.utils.LogInfo;
import com.plbear.iweight.utils.Utils;

import java.util.ArrayList;

/**
 * Created by yanyongjun on 2018/6/24.
 */

public class DataManager {
    private static final String TAG = "DataManager";
    private Context mContext;
    private ContentResolver mResolver;
    private static DataManager sInstance;

    private DataManager(Context context) {
        mContext = context;
        mResolver = mContext.getContentResolver();
    }

    public void delete(ArrayList<Data> list) {
        if (list == null || list.size() == 0) {
            LogInfo.e(TAG, "list size == 0");
            return;
        }
        StringBuffer strId = new StringBuffer();
        strId.append(list.get(0).getId());
        for (int i = 1; i < list.size(); i++) {
            strId.append(",");
            strId.append(list.get(i).getId());
        }
        LogInfo.i(TAG, "normal_delete:" + strId.toString());
        mResolver.delete(Constant.CONTENT_URI, "_id in (" + strId.toString() + ")", null);
    }

    public void update(Data data) {
        if (data == null) {
            LogInfo.e(TAG, "update error");
            return;
        }
        ContentValues values = new ContentValues();
        values.put("weight", data.getWeight() / Utils.getValueUnit());
        LogInfo.i(TAG, "data:" + data.toString());
        mResolver.update(Constant.CONTENT_URI, values, "_id in (?)", new String[]{data.getId() + ""});
    }

    /**
     * 插入一组数据
     *
     * @param data
     */
    public void add(Data data) {
        if (data == null) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put("time", data.getTime()+"");
        values.put("weight", (data.getWeight() / Utils.getValueUnit())+"");
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
            values.put("time", data.getTime()+"");
            values.put("weight", (data.getWeight() / Utils.getValueUnit())+"");
            if (lastOne == data) {
                mResolver.insert(Constant.CONTENT_URI, values);
            } else {
                mResolver.insert(Constant.CONTENT_URI_WITHOUT_NOTIRY, values);
            }
        }
        return;
    }
    public Long queryLastDataTime() {
        Cursor cursor = mResolver.query(Constant.CONTENT_URI, new String[]{"max(time)"}, null, null, null);
        try {
            cursor.moveToFirst();
            return cursor.getLong(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return -1L;
    }

    public ArrayList<Data> queryAll() {
        Uri uri = Constant.CONTENT_URI;
        Cursor cursor = mResolver.query(uri, new String[]{"_id", "time", "weight"}, null, null, null);
        ArrayList list = new ArrayList<Data>();
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Data data = new Data(cursor.getInt(0), cursor.getLong(1),
                        cursor.getFloat(2) * Utils.getValueUnit());
                list.add(data);
                cursor.moveToNext();
            }
        } catch (Exception e) {
        } finally {
            cursor.close();
        }

        return list;
    }

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

    public static DataManager getInstance() {
        if (sInstance != null) {
            return sInstance;
        }
        sInstance = new DataManager(App.getAppContext());
        return sInstance;
    }

}
