package com.plbear.iweight.base;

import android.os.Handler;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.AdKeys;
import com.iflytek.voiceads.IFLYAdListener;
import com.iflytek.voiceads.IFLYAdSize;
import com.iflytek.voiceads.IFLYInterstitialAd;
import com.plbear.iweight.R;
import com.plbear.iweight.model.details.ui.DetailsFragment;
import com.plbear.iweight.model.form.ui.DataFragment;
import com.plbear.iweight.model.main.ui.MainFragment;
import com.plbear.iweight.model.me.ui.MeFragment;
import com.plbear.iweight.utils.Utils;


import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {
    private String[] titles = {"主页", "数据", "详细", "我的"};
    private Class[] fragments = {MainFragment.class, DataFragment.class, DetailsFragment.class, MeFragment.class};
    private boolean mIsExiting = false;
    private android.os.Handler mHandler = new Handler();
    private boolean isShowedAd = false;

    @BindView(R.id.tab_host)
    FragmentTabHost mTabHost;

    IFLYInterstitialAd ad = null;
    IFLYAdListener mAdListener = new IFLYAdListener() {
        @Override
        public void onAdReceive() {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ad.showAd();
                    isShowedAd = true;
                }
            }, 400);
        }

        @Override
        public void onAdFailed(AdError adError) {

        }

        @Override
        public void onAdClick() {

        }

        @Override
        public void onAdClose() {

        }

        @Override
        public void onAdExposure() {

        }

        @Override
        public void onConfirm() {

        }

        @Override
        public void onCancel() {

        }
    };

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void afterLayout() {
        initTagWidget();
    }

    private void initTagWidget() {
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        for (int i = 0; i < 4; i++) {
            View v = LayoutInflater.from(this).inflate(R.layout.tab_host, null);
            TextView lab = v.findViewById(R.id.tv_tab);
            lab.setText(titles[i]);
            mTabHost.addTab(mTabHost.newTabSpec("" + i).setIndicator(v), fragments[i], null);
        }

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                for (int i = 0; i < 4; i++) {
                    View v = mTabHost.getTabWidget().getChildTabViewAt(i);
                    TextView lab = v.findViewById(R.id.tv_tab);
                    lab.setTextColor(getResources().getColor(R.color.settings_category_text));
                }

                View v = mTabHost.getCurrentTabView();
                TextView lab = v.findViewById(R.id.tv_tab);
                lab.setTextColor(getResources().getColor(R.color.keyboard_key_submit_pressed));

                if (!isShowedAd && mTabHost.getCurrentTab() != 0) {
                    /**
                     * 加载广告
                     */
                    int ramdom = ((int) (Math.random() * 10)) % 10;
                    logerror("ramdom:" + ramdom);

                    if ( ramdom < 5) {
                        ad = IFLYInterstitialAd.createInterstitialAd(MainActivity.this, Constant.AD_ID_Main);
                        ad.setAdSize(IFLYAdSize.INTERSTITIAL);
                        ad.setParameter(AdKeys.BACK_KEY_ENABLE, "true");
                        ad.loadAd(mAdListener);
                    }
                }

            }
        });
        mTabHost.setCurrentTabByTag("0");
        View v = mTabHost.getTabWidget().getChildTabViewAt(0);
        TextView lab = v.findViewById(R.id.tv_tab);
        lab.setTextColor(getResources().getColor(R.color.keyboard_key_submit_pressed));
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 双击退出
     */
    private void exitBy2Click() {
        if (!mIsExiting) {
            mIsExiting = true;
            Utils.showToast("再按一次退出程序");
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mIsExiting = false;
                }
            }, 2000);
        } else {
            exitAll();
        }
    }
}