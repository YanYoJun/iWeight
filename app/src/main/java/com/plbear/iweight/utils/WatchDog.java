package com.plbear.iweight.utils;

/**
 * Created by yanyongjun on 2018/4/21.
 */

import android.os.Handler;
import android.os.Looper;

import java.util.logging.LogRecord;

/**
 * 实现开门狗机制，当过期没有响应的时候，触发onTimeoutListener
 */
public class WatchDog {
    public interface WatchDogListener {
        void onTimeoutListener();

        void onCancelListener();
    }

    private Handler mHandler = new Handler();
    private WatchDogListener mListener = null;
    private int mStatus = 0; //0:init  1:started 2:killed
    private int mTime = 2000; //timeout time

    public WatchDog(WatchDogListener listener) {
        this(listener, 2000);
    }

    public WatchDog(WatchDogListener listener, int time) {
        mListener = listener;
        mTime = time;
    }

    public boolean start() {
        if (mStatus != 0) {
            return false;
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mStatus == 1) {
                    mStatus = 2;
                    mListener.onTimeoutListener();
                }
            }
        }, mTime);
        mStatus = 1;
        return true;
    }

    public void stop() {
        mStatus = 0;
        mListener.onCancelListener();
    }
}
