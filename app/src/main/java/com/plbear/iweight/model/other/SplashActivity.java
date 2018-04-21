package com.plbear.iweight.model.other;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.plbear.iweight.R;
import com.plbear.iweight.base.BaseActivity;
import com.plbear.iweight.model.main.activity.MainActivity;
import com.plbear.iweight.utils.LogInfo;
import com.plbear.iweight.utils.MyLog;
import com.plbear.iweight.utils.PermissionHelper;
import com.plbear.iweight.utils.WatchDog;

import sd.sazs.erd.AdManager;
import sd.sazs.erd.nm.cm.ErrorCode;
import sd.sazs.erd.nm.sp.SplashViewSettings;
import sd.sazs.erd.nm.sp.SpotListener;
import sd.sazs.erd.nm.sp.SpotManager;
import sd.sazs.erd.nm.sp.SpotRequestListener;

/**
 * Created by yanyonigjun on 2018/4/15.
 */

public class SplashActivity extends BaseActivity {
    private final static String TAG = "SplashActivity";
    private PermissionHelper mPermissionHelper;
    private Context mContext = null;
    private boolean mIsFirstLoad = false;

    private Handler mHandler = new Handler();

    @Override
    public int getLayout() {
        return R.layout.activity_splash;
    }

    @Override
    public void afterLayout() {
        mContext = this;
        // 当系统为6.0以上时，需要申请权限
        mPermissionHelper = new PermissionHelper(this);
        mPermissionHelper.setOnApplyPermissionListener(new PermissionHelper.OnApplyPermissionListener() {
            @Override
            public void onAfterApplyAllPermission() {
                Log.e(TAG, "All of requested permissions has been granted, so run app logic.");
                runApp();
            }
        });
        if (Build.VERSION.SDK_INT < 23) {
            // 如果系统版本低于23，直接跑应用的逻辑
            Log.e(TAG, "The api level of system is lower than 23, so run app logic directly.");
            runApp();
        } else {
            // 如果权限全部申请了，那就直接跑应用逻辑
            if (mPermissionHelper.isAllRequestedPermissionGranted()) {
                Log.e(TAG, "All of requested permissions has been granted, so run app logic directly.");
                runApp();
            } else {
                // 如果还有权限为申请，而且系统版本大于23，执行申请权限逻辑
                Log.e(TAG, "Some of requested permissions hasn't been granted, so apply permissions first.");
                mPermissionHelper.applyPermissions();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPermissionHelper.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 跑应用的逻辑
     */
    private void runApp() {
        loginfo("runApp in");
        //初始化SDK
        AdManager.getInstance(mContext).init("2119c919e5e59eb2", "2c27707a36d1316a", true);
        loginfo("runApp out");
        preloadAd();
        //setupSplashAd(); // 如果需要首次展示开屏，请注释掉本句代码
    }

    /**
     * 预加载广告
     */
    private void preloadAd() {
        loginfo("preloadAd in");
        //增加一个看门狗，预防SDK没有像响应
        final WatchDog watchDog = new WatchDog(new WatchDog.WatchDogListener() {
            @Override
            public void onTimeoutListener() {
                logerror("watch dog time out,start main activity");
                startMainActivity();
                finish();
            }

            @Override
            public void onCancelListener() {
                loginfo("watch dog is canceled");
            }
        },3000);
        watchDog.start();
        loginfo("watch dog is start");

        // 注意：不必每次展示插播广告前都请求，只需在应用启动时请求一次
        SpotManager.getInstance(mContext).requestSpot(new SpotRequestListener() {
            @Override
            public void onRequestSuccess() {
                MyLog.Companion.d(TAG, "请求插播广告成功");
                //				// 应用安装后首次展示开屏会因为本地没有数据而跳过
                //              // 如果开发者需要在首次也能展示开屏，可以在请求广告成功之前展示应用的logo，请求成功后再加载开屏
                watchDog.stop();
                setupSplashAd();
            }

            @Override
            public void onRequestFailed(int errorCode) {
                watchDog.stop();
                logerror(String.format("请求插播广告失败，errorCode: %s", errorCode));
                switch (errorCode) {
                    case ErrorCode.NON_NETWORK:
                        loginfo("网络异常");
                        break;
                    case ErrorCode.NON_AD:
                        loginfo("暂无视频广告");
                        break;
                    default:
                        loginfo("请稍后再试");
                        break;
                }
                startMainActivity();
                finish();
            }
        });
        loginfo("preloadAd out");
    }

    private void startMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    /**
     * 设置开屏广告
     */
    private void setupSplashAd() {
        loginfo("setupSplashAd in");
        // 创建开屏容器

        final RelativeLayout splashLayout = (RelativeLayout) findViewById(R.id.rl_splash);
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ABOVE, R.id.view_divider);

        // 对开屏进行设置
        SplashViewSettings splashViewSettings = new SplashViewSettings();
        //		// 设置是否展示失败自动跳转，默认自动跳转
        //		splashViewSettings.setAutoJumpToTargetWhenShowFailed(false);
        // 设置跳转的窗口类
        splashViewSettings.setTargetClass(MainActivity.class);
        // 设置开屏的容器
        splashViewSettings.setSplashViewContainer(splashLayout);

        // 展示开屏广告
        SpotManager.getInstance(mContext)
                .showSplash(mContext, splashViewSettings, new SpotListener() {

                    @Override
                    public void onShowSuccess() {
                        LogInfo.d(TAG, "开屏展示成功");
                    }

                    @Override
                    public void onShowFailed(int errorCode) {
                        LogInfo.e(TAG, "开屏展示失败");
                        switch (errorCode) {
                            case ErrorCode.NON_NETWORK:
                                LogInfo.e(TAG, "网络异常");
                                break;
                            case ErrorCode.NON_AD:
                                LogInfo.e(TAG, "暂无开屏广告");
                                break;
                            case ErrorCode.RESOURCE_NOT_READY:
                                LogInfo.e(TAG, "开屏资源还没准备好");
                                break;
                            case ErrorCode.SHOW_INTERVAL_LIMITED:
                                LogInfo.e(TAG, "开屏展示间隔限制");
                                break;
                            case ErrorCode.WIDGET_NOT_IN_VISIBILITY_STATE:
                                LogInfo.e(TAG, "开屏控件处在不可见状态");
                                break;
                            default:
                                LogInfo.e(TAG, String.format("errorCode: %d", errorCode));
                                break;
                        }
                        startMainActivity();
                    }

                    @Override
                    public void onSpotClosed() {
                        LogInfo.d(TAG, "开屏被关闭");
                        startMainActivity();
                    }

                    @Override
                    public void onSpotClicked(boolean isWebPage) {
                        LogInfo.d(TAG, "开屏被点击");
                        loginfo(String.format("是否是网页广告？%s", isWebPage ? "是" : "不是"));
                    }
                });
        loginfo("setupSplashInfo out");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 开屏展示界面的 onDestroy() 回调方法中调用
        SpotManager.getInstance(mContext).onDestroy();
    }
}

