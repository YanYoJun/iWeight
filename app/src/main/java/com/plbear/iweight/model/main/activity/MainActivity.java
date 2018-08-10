package com.plbear.iweight.model.main.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.plbear.iweight.R;
import com.plbear.iweight.base.BaseActivity;
import com.plbear.iweight.data.Data;
import com.plbear.iweight.data.DataManager;
import com.plbear.iweight.model.details.DetailsActivity;
import com.plbear.iweight.model.main.fragment.MainDataFragment;
import com.plbear.iweight.model.main.view.KeyboardBuilder;
import com.plbear.iweight.model.main.view.MyKeyboradView;
import com.plbear.iweight.model.other.AboutActivity;
import com.plbear.iweight.model.settings.SettingsActivity;
import com.plbear.iweight.storage.XMLHelper;
import com.plbear.iweight.utils.LogInfo;
import com.plbear.iweight.utils.SPUtils;
import com.plbear.iweight.utils.Utils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Timer;

import butterknife.BindView;

/**
 * Created by yanyongjun on 2018/6/30.
 */

public class MainActivity extends BaseActivity {
    public final static int REQUSET_IMPORT_CODE_PERMISSION = 1;
    public final static int REQUSET_EXPORT_CODE_PERMISSION = 2;
    private XMLHelper.OnXMLListener mXmlListener;
    private ArrayList<MainDataFragment> mFragList = new ArrayList<>();
    private boolean mIsExiting = false;
    private FragmentPagerAdapter mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
        @Override
        public Fragment getItem(int position) {
            return mFragList.get(position);
        }

        @Override
        public int getCount() {
            return mFragList.size();
        }
    };

    @BindView(R.id.btn_record)
    Button mBtnRecord;

    @BindView(R.id.btn_title_more)
    ImageButton mBtnTitleMore;

    @BindView(R.id.drawer_layout_main)
    DrawerLayout mDrawLayoutMain;

    @BindView(R.id.nav_main_view)
    NavigationView mNavView;

    @BindView(R.id.view_pager_main)
    ViewPager mViewPager;

    @BindView(R.id.keyboard_main)
    MyKeyboradView myKeyboardView;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void afterLayout() {
        init();
        mBtnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean onceEveryDay = SPUtils.getSP().getBoolean(SettingsActivity.PREFERENCE_KEY_ONLY_ONCE_EVERYDAY, true);
                if (onceEveryDay && !Utils.DEBUG) {
                    String lastTime = Utils.formatTime(DataManager.getInstance().queryLastDataTime());
                    if (lastTime.equals(Utils.formatTime(System.currentTimeMillis()))) {
                        Utils.showToast(R.string.main_toast_notify_only_once);
                        return;
                    }
                }
                recordWeight();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });
        mBtnTitleMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawLayoutMain.openDrawer(mNavView);
            }
        });
    }

    private void init() {
        initNav();
        mXmlListener = new XMLHelper.OnXMLListener() {
            @Override
            public void onReadSuccess() {
                Utils.showToast("文件读取成功");
            }

            @Override
            public void onSaveSuccess() {
                Utils.showToast("恭喜您，数据成功导出");
            }

            @Override
            public void onReadFail() {
                Utils.showToast("读取失败，请检查文件是否存在");
            }

            @Override
            public void onSaveFail() {
                Utils.showToast("数据导出失败");
            }
        };
        initCharView();
    }

    private void initNav() {
        View btnAbout = mNavView.getHeaderView(0).findViewById(R.id.btn_main_nav_about);
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(i);
            }
        });

        View exitButton = mNavView.getHeaderView(0).findViewById(R.id.btn_main_nav_exit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitAll();
            }
        });

        View detailsButton = mNavView.getHeaderView(0).findViewById(R.id.btn_main_nav_detail);
        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, DetailsActivity.class);
                startActivity(i);
            }
        });

        View settingsButton = mNavView.getHeaderView(0).findViewById(R.id.btn_main_nav_settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });
    }

    @BindView(R.id.tab_main)
    TabLayout mTabMain;

    private void initTabLayout() {
        try {
            mTabMain.setupWithViewPager(mViewPager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mTabMain.setTabMode(TabLayout.MODE_FIXED);
        mTabMain.getTabAt(0).setText("本周");
        mTabMain.getTabAt(1).setText("当月");
        mTabMain.getTabAt(2).setText("全部");
    }

    /**
     * 初始化折线图
     */
    private void initCharView() {
        //init fragment
        MainDataFragment weekFrag = new MainDataFragment();
        weekFrag.setTag("weekFrag");
        weekFrag.setShowDateNums(7);
        mFragList.add(weekFrag);

        MainDataFragment monthFrag = new MainDataFragment();
        monthFrag.setTag("monthFrag");
        monthFrag.setShowDateNums(30);
        mFragList.add(monthFrag);

        MainDataFragment allFrag = new MainDataFragment();
        allFrag.setTag("allFrag");
        allFrag.setShowAllData(true);
        mFragList.add(allFrag);

        mViewPager.setAdapter(mPagerAdapter);
        initTabLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean isExOn = SPUtils.getSP().getBoolean(SettingsActivity.PREFERENCE_KEY_EXPORT_IMPORT, false);
        Button importButton = mNavView.getHeaderView(0).findViewById(R.id.btn_main_nav_import);
        if (isExOn) {
            importButton.setVisibility(View.VISIBLE);
            importButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    XMLHelper xmlHelper = new XMLHelper(MainActivity.this);
                    if (Build.VERSION.SDK_INT >= 23) {
                        int checkPermission = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
                        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS},
                                    REQUSET_IMPORT_CODE_PERMISSION);
                            return;
                        }
                    }
                    xmlHelper.readXML(mXmlListener);
                }
            });
        } else {
            importButton.setVisibility(View.GONE);
        }


        /**
         * 导出
         */
        Button exportButton = mNavView.getHeaderView(0).findViewById(R.id.btn_main_nav_export);
        if (isExOn) {
            exportButton.setVisibility(View.VISIBLE);
            exportButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    XMLHelper helper = new XMLHelper(MainActivity.this);
                    if (Build.VERSION.SDK_INT >= 23) {
                        int checkPermission = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
                        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, REQUSET_EXPORT_CODE_PERMISSION);
                            return;
                        }
                    }
                    helper.saveXML(mXmlListener);
                }
            });
        } else {
            exportButton.setVisibility(View.GONE);
        }
    }

    @BindView(R.id.edit_num)
    EditText mEditText;


    /**
     * 弹出记录体重的弹框
     */
    private void recordWeight() {
        mBtnRecord.setVisibility(View.GONE);
        mEditText.setVisibility(View.VISIBLE);
        mEditText.setText("");
        KeyboardBuilder keyboardBuilder = new KeyboardBuilder(MainActivity.this, myKeyboardView, R.xml.main_keyboard, mEditText, new KeyboardBuilder.OnStatusChanged() {
            @Override
            public void onChanged(int temp) {
                switch (temp) {
                    case KeyboardBuilder.STATUS_CANCEL:
                        mBtnRecord.setVisibility(View.VISIBLE);
                        mEditText.setVisibility(View.GONE);
                        break;
                    case KeyboardBuilder.STATUS_SUBMIT:
                        mBtnRecord.setVisibility(View.VISIBLE);
                        mEditText.setVisibility(View.GONE);
                        long time = System.currentTimeMillis();
                        loginfo("mEditText:" + mEditText.getText());
                        Data data = new Data(-1, time, Float.parseFloat(mEditText.getText().toString()));
                        boolean isOlnyOneTime = SPUtils.getSP().getBoolean(SettingsActivity.PREFERENCE_KEY_ONLY_ONCE_EVERYDAY, true);
                        DataManager.getInstance(MainActivity.this).add(data);
                }
            }
        });
        keyboardBuilder.showKeyboard(mEditText);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        XMLHelper helper = null;
        switch (requestCode) {
            case REQUSET_EXPORT_CODE_PERMISSION: {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "请在“设置->应用->权限”中赋予权限后重新执行", Toast.LENGTH_SHORT).show();
                    return;
                }

                helper = new XMLHelper(this);
                helper.saveXML(mXmlListener);
                break;
            }
            case REQUSET_IMPORT_CODE_PERMISSION: {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "请在“设置->应用->权限”赋予权限后重新执行", Toast.LENGTH_SHORT).show();
                    return;
                }

                helper = new XMLHelper(this);
                helper.readXML(mXmlListener);
                break;
            }
        }
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