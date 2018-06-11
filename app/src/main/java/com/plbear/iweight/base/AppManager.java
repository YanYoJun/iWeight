package com.plbear.iweight.base;

import android.app.Activity;

import java.util.Stack;

/**
 * Created by yanyongjun on 2018/6/11.
 */

public class AppManager {
    private static AppManager mInstance = null;
    private static Stack<Activity> mActivities = new Stack<>();

    public static AppManager getAppManager() {
        if (mInstance != null) {
            return mInstance;
        }
        mInstance = new AppManager();
        return mInstance;
    }

    private AppManager() {

    }

    public void addToStack(Activity activity) {
        mActivities.push(activity);
    }

    public void removeFromStack(Activity activity) {
        mActivities.remove(activity);
    }

    public void finishAllActivity() {
        while (!mActivities.isEmpty()) {
            Activity activity = mActivities.pop();
            activity.finish();
        }
    }
}