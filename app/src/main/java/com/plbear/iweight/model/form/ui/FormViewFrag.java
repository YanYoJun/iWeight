package com.plbear.iweight.model.form.ui;

import android.support.v4.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.plbear.iweight.R;
import com.plbear.iweight.base.Constant;
import com.plbear.iweight.model.form.adapter.LineChartAdapter;
import com.plbear.iweight.model.form.view.LineChartView;

/**
 * Created by yanyongjun on 2018/6/27.
 */

public class FormViewFrag extends Fragment {
    private String TAG = "FormViewFrag--";
    public final static String ACTION_DATA_CHANED = "com.plbear.iweight.data_changed";
    private LineChartView mView;
    private LineChartAdapter mAdapter;
    private int mShowNum = 7;
    private boolean mShowAllData = false;
    private Handler mHandler = new Handler();
//    private TextView mValley;
//    private TextView mPeak;
    private boolean mDataChanged = false;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_DATA_CHANED.equals(action)) {
                mDataChanged = true;
            }
        }
    };

    private ContentObserver mObserver = new ContentObserver(mHandler) {
        @Override
        public void onChange(boolean selfChange) {
            mAdapter.notifyDataSetChange();
            super.onChange(selfChange);
        }
    };

    public void setTag(String tag) {
        TAG += tag;
    }

    public void setShowDateNums(int num) {
        mShowNum = num;
    }

    public void setShowAllData(boolean flag) {
        mShowAllData = flag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_data, null);
        mView = (LineChartView) v.findViewById(R.id.show_weight);
        mAdapter = mView.getDataAdpater();
        return v;
    }

    @Override
    public void onStart() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_DATA_CHANED);
        getActivity().registerReceiver(mReceiver, filter);
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.setShowNum(mShowNum);
        mAdapter.setShowAllData(mShowAllData);
        getActivity().getContentResolver().registerContentObserver(Constant.CONTENT_URI, true, mObserver);
        if (mDataChanged) {
            mAdapter.notifyDataSetChange();
            mDataChanged = false;
        }
    }

    @Override
    public void onPause() {
        getActivity().getContentResolver().unregisterContentObserver(mObserver);
        super.onPause();
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(mReceiver);
        super.onStop();
    }
}
