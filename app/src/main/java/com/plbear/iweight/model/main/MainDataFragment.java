package com.plbear.iweight.model.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.plbear.iweight.R;
import com.plbear.iweight.Utils.Constant;
import com.plbear.iweight.Utils.ILog;
import com.plbear.iweight.view.DataAdapter;
import com.plbear.iweight.view.LineChartView;

/**
 * Created by yanyongjun on 2017/7/22.
 */

public class MainDataFragment extends Fragment {
    public final static String ACTION_DATA_CHANED = "com.plbear.iweight.data_changed";

    private String TAG = "MainDataFragment-" + this + "--";
    private LineChartView mView = null;
    private DataAdapter mAdapter = null;
    private int mShowNum = 7;
    private boolean mShowAllData = false;
    private Handler mHandler = new Handler();
    private TextView mValley = null;
    private TextView mPeak = null;
    private boolean mDataChanged = false;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            ILog.i(TAG, "action:" + action);
            if (ACTION_DATA_CHANED.equals(action)) {
                mDataChanged = true;
            }
        }
    };


    private ContentObserver mObserver = new ContentObserver(mHandler) {
        @Override
        public void onChange(boolean selfChange) {
            ILog.e(TAG, "mObserver onChange");
            mAdapter.notifyDataSetChange();
            mValley.setText(String.valueOf(mAdapter.getTrueWeightSmallest() + 5));
            mPeak.setText(String.valueOf(mAdapter.getTrueWeightBiggest() - 5));
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
        ILog.e(TAG, "setShowAllData:" + flag);
        mShowAllData = flag;
    }

    @Override
    public void onStart() {
        ILog.i(TAG, "onStart");
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_DATA_CHANED);
        getActivity().registerReceiver(mReceiver, filter);
        super.onStart();
    }

    @Override
    public void onResume() {
        ILog.i(TAG, "onResume");
        super.onResume();
        mAdapter.setShowNum(mShowNum);
        mAdapter.setShowAllData(mShowAllData);
        mValley.setText(String.valueOf(mAdapter.getTrueWeightSmallest() + 5));
        mPeak.setText(String.valueOf(mAdapter.getTrueWeightBiggest() - 5));
        getActivity().getContentResolver().registerContentObserver(Constant.CONTENT_URI, true, mObserver);
        if (mDataChanged) {
            mAdapter.notifyDataSetChange();
            mValley.setText(String.valueOf(mAdapter.getTrueWeightSmallest() + 5));
            mPeak.setText(String.valueOf(mAdapter.getTrueWeightBiggest() - 5));
            mDataChanged = false;
        }
    }

    @Override
    public void onPause() {
        ILog.i(TAG, "onPause");
        getActivity().getContentResolver().unregisterContentObserver(mObserver);
        getActivity().unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        ILog.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onStop() {
        ILog.i(TAG, "onStop");

        super.onStop();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ILog.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ILog.i(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_line_chart_view, null);
        mView = (LineChartView) v.findViewById(R.id.show_weight);
        mAdapter = mView.getDataAdpater();
        mValley = (TextView) v.findViewById(R.id.lab_lowest_values);
        mPeak = (TextView) v.findViewById(R.id.lab_highest_value);
        return v;
    }


}
