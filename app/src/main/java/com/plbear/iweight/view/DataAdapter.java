package com.plbear.iweight.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.SparseArray;

import com.plbear.iweight.Data.Data;
import com.plbear.iweight.Data.DataManager;
import com.plbear.iweight.Utils.ILog;
import com.plbear.iweight.Utils.OldUtils;
import com.plbear.iweight.Utils.SPUtils;
import com.plbear.iweight.model.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by yanyongjun on 16/11/5.
 */

public class DataAdapter {
    private String TAG = "DataAdapter:";
    private ArrayList<Data> mDataList = new ArrayList<Data>();
    private ArrayList<Data> mShowList = new ArrayList<Data>();
    private float mWeightBiggest = 0;
    private float mWeightSmallest = 0;
    private long mBeginTime = 0;
    private long mEndTime = 0;

    private ArrayList<DataChangeListener> mListener = new ArrayList<DataChangeListener>();
    private Context mContext = null;
    //目标体重值
    private float mTagetWeight = -1;
    private DataManager mDataManager = null;
    private final int DEFAULT_NUMS = 7;
    private int mShowPointCount = DEFAULT_NUMS;

    private int mShowBeginId;

    private boolean mShowAllData = false;

    public DataAdapter(Context context) {
        init(context);
    }


    public int getShowPointCount() {
        return mShowAllData ? mShowList.size() : mShowPointCount;
    }

    public void setShowAllData(boolean flag) {
        mShowAllData = flag;
        notifyDataSetChange();
    }

    public void setTag(String tag) {
        TAG += tag;
        ILog.e(TAG, "setTag");
    }

    /**
     * init 初始化各个参数值，在构造方法中进行调用
     *
     * @returnd
     */
    private void init(Context context) {
        mContext = context;
        SharedPreferences sp = SPUtils.Companion.getSP(mContext);
        mTagetWeight = sp.getFloat(SettingsActivity.PREFERENCE_KEY_SET_TARGET_WEIGHT, -1) *
                OldUtils.getValueUnit(context);

        DataManager db = DataManager.getInstance(context);
        ArrayList<Data> list = db.queryAll();
        mDataList.clear();
        mDataList.addAll(list);

        setShowList(mDataList);

        mDataManager = DataManager.getInstance(mContext);
    }

    public void setShowNum(int num) {
        mShowPointCount = num;
        notifyDataSetChange();
    }

    public interface DataChangeListener {
        public void onChange();
    }


    public void registerDataListener(DataChangeListener listener) {
        mListener.add(listener);
    }


    /**
     * 通知注册Adapter Lisntenr的类进行刷新
     */
    private void notifyDataChange() {
        SharedPreferences sp = SPUtils.Companion.getSP(mContext);
        mTagetWeight = sp.getFloat(SettingsActivity.PREFERENCE_KEY_SET_TARGET_WEIGHT, -1)
                * OldUtils.getValueUnit(mContext);
        for (DataChangeListener listener : mListener) {
            listener.onChange();
        }
    }

    public void notifyDataSetChange() {
        ILog.e(TAG, "notifyDataSetChange");
        DataManager db = DataManager.getInstance(mContext);
        ArrayList<Data> list = db.queryAll();
        mDataList.clear();
        mDataList.addAll(list);

        setShowList(list);

        notifyDataChange();
    }

    public void notifyDataPosSetChange(float moveLength) {
        //TODO
    }

    private void setShowList(ArrayList<Data> allList) {
        if (allList == null || allList.size() == 0) {
            mShowList.clear();
            mWeightBiggest = 0;
            mWeightSmallest = 0;
            mBeginTime = 0;
            mEndTime = 0;
            return;
        }
        ILog.e(TAG, "setShowList:" + mShowPointCount + ":mShowAllData:" + mShowAllData);
        if (mShowAllData) {
            mShowList = allList;
            mShowBeginId = mDataList.get(0).getId();
            SparseArray<String> list = resolveShowList();
            mEndTime = Long.valueOf(list.get(1));
            mBeginTime = Long.valueOf(list.get(2));
            mWeightBiggest = Float.valueOf(list.get(3));
            mWeightSmallest = Float.valueOf(list.get(4));
            return;
        }
        if (mDataList.size() <= mShowPointCount) {
            mShowBeginId = mDataList.get(0).getId();
        } else {
            mShowBeginId = mDataList.get(mDataList.size() - (int) mShowPointCount).getId();
        }

        mShowList.clear();
        boolean add = false;
        int count = 0;
        for (Data data : allList) {
            if (data.getId() == mShowBeginId) {
                add = true;
            }
            if (add) {
                count++;
                if (count > mShowPointCount) {
                    break;
                }
                mShowList.add(data);
            }
        }
        SparseArray<String> list = resolveShowList();
        mEndTime = Long.valueOf(list.get(1));
        mBeginTime = Long.valueOf(list.get(2));
        mWeightBiggest = Float.valueOf(list.get(3));
        mWeightSmallest = Float.valueOf(list.get(4));
    }

    private void sort() {
        Collections.sort(mDataList, new Comparator<Data>() {
            @Override
            public int compare(Data data, Data data2) {
                if (data.getTime() > data2.getTime()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }

    /**
     * 得到目标体重，这个体重刷新后的数据来源应用该是唯一的，就是这里
     *
     * @return
     */
    public float getTargetWeight() {
        return mTagetWeight;
    }

    public ArrayList<Data> getShowDataList() {
        return mShowList;
    }

    /**
     * 返回要展示的list的第一个id
     *
     * @return
     */
    public int getShowDataStartId() {
        //TODO
        return mShowBeginId;
    }

    public long getTimeSmallest() {
        return mBeginTime;
    }

    public long getTimeBiggest() {
        return mEndTime;
    }

    public float getWeightBiggest() {

        if (mTagetWeight == -1) {
            return getTrueWeightBiggest();
        }
        return (mWeightBiggest > mTagetWeight ? mWeightBiggest : mTagetWeight) + 5;
    }

    public float getWeightSmallest() {
        if (mTagetWeight == -1) {
            return getTrueWeightSmallest();
        }
        return (mWeightSmallest < mTagetWeight ? mWeightSmallest : mTagetWeight) - 5;
    }

    public float getTrueWeightBiggest() {
        return mWeightBiggest + 5;
    }

    public float getTrueWeightSmallest() {
        return mWeightSmallest - 5;
    }


    public SparseArray<String> resolveShowList() {
        SparseArray<String> list = new SparseArray<String>();
        long timeBiggest = 0;
        long timeSmallest = Long.MAX_VALUE;
        float weightBiggest = 0;
        float weightSmallest = Float.MAX_VALUE;
        for (Data data : mShowList) {
            if (timeBiggest <= data.getTime()) {
                timeBiggest = data.getTime();
            }
            if (timeSmallest >= data.getTime()) {
                timeSmallest = data.getTime();
            }
            if (weightBiggest <= data.getWeight()) {
                weightBiggest = data.getWeight();
            }
            if (weightSmallest >= data.getWeight()) {
                weightSmallest = data.getWeight();
            }
        }
        list.put(1, String.valueOf(timeBiggest));
        list.put(2, String.valueOf(timeSmallest));
        list.put(3, String.valueOf(weightBiggest));
        list.put(4, String.valueOf(weightSmallest));
        return list;
    }


    public float getHeight() {
        return getWeightBiggest() - getWeightSmallest();
    }


}
