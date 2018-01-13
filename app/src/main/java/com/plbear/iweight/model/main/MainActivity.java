package com.plbear.iweight.model.main;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.plbear.iweight.Data.Data;
import com.plbear.iweight.Data.DataManager;
import com.plbear.iweight.R;
import com.plbear.iweight.Utils.ILog;
import com.plbear.iweight.Utils.SPUtils;
import com.plbear.iweight.Utils.OldUtils;
import com.plbear.iweight.Utils.Utils;
import com.plbear.iweight.activity.AboutActivity;
import com.plbear.iweight.model.settings.SettingsActivity;
import com.plbear.iweight.model.details.DetailsActivity;
import com.plbear.iweight.storage.XMLHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yanyongjun on 16/11/5.
 */

public class MainActivity extends FragmentActivity {
    private DataManager mDB = null;
    private final static String TAG = "MainActivity";
    //private LineChartView mShowView = null;
    private ViewPager mViewPager = null;
    private TextView mWeek = null;
    private TextView mMonth = null;
    private TextView mAll = null;
    private SparseArray<TextView> mSwitchLab = new SparseArray<>();

    private NavigationView mNavView = null;
    private DrawerLayout mDrawerLayout = null;
    public final static int REQUSET_IMPORT_CODE_PERMISSION = 1;
    public final static int REQUSET_EXPORT_CODE_PERMISSION = 2;
    private XMLHelper.OnXMLListener mXmlListener = null;
    private SharedPreferences mSP = null;
    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        Button btn = (Button) findViewById(R.id.btn_record);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ILog.d(TAG, "input weight");
                boolean onceEveryDay = mSP.getBoolean(SettingsActivity.PREFERENCE_KEY_ONLY_ONCE_EVERYDAY, true);
                if (onceEveryDay) {
                    String lastTime = OldUtils.formatTime(mDB.queryLastDataTime());
                    ILog.d(TAG, "lastTime:" + lastTime);
                    if (lastTime.equals(OldUtils.formatTime(System.currentTimeMillis()))) {
                        Toast.makeText(MainActivity.this, R.string.toast_notify_only_once_everyday, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                showRecordDialog();
            }
        });
        //mShowView = (LineChartView) findViewById(R.id.show_weight);

        ImageButton btnMore = (ImageButton) findViewById(R.id.btn_title_more);
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(mNavView);
            }
        });
    }

    private void init() {
        mDB = DataManager.getInstance(this);
        mNavView = (NavigationView) findViewById(R.id.nav_main_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        mSP = SPUtils.Companion.getSP(this);


        Button btnAbout = (Button) mNavView.getHeaderView(0).findViewById(R.id.btn_main_nav_about);
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        Button exitButton = (Button) mNavView.getHeaderView(0).findViewById(R.id.btn_main_nav_exit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });

        mXmlListener = new XMLHelper.OnXMLListener() {
            @Override
            public void onReadSuccess() {
                Toast.makeText(MainActivity.this, "文件读取成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSaveSuccess() {
                Toast.makeText(MainActivity.this, "恭喜您，数据成功导出", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReadFail() {
                Toast.makeText(MainActivity.this, "读取失败，请检查文件是否存在", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSaveFail() {
                Toast.makeText(MainActivity.this, "数据导出失败", Toast.LENGTH_SHORT).show();
            }
        };


        Button detailsButton = (Button) mNavView.getHeaderView(0).findViewById(R.id.btn_main_nav_detail);
        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ILog.d(TAG, "detailsButton click");
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                startActivity(intent);
            }
        });

        Button settingsButton = (Button) mNavView.getHeaderView(0).findViewById(R.id.btn_main_nav_settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ILog.d(TAG, "settingsButton click");
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        //init week\month\year\all TextView
        mWeek = (TextView) findViewById(R.id.lab_week);
        mMonth = (TextView) findViewById(R.id.lab_month);
        mAll = (TextView) findViewById(R.id.lab_all_data);
        mSwitchLab.put(0, mWeek);
        mSwitchLab.put(1, mMonth);
        mSwitchLab.put(2, mAll);

        mWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFrag(0);
                mViewPager.setCurrentItem(0);
            }
        });

        mMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFrag(1);
                mViewPager.setCurrentItem(1);
            }
        });


        mAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFrag(3);
                mViewPager.setCurrentItem(3);
            }
        });

        //init fragment
        mViewPager = (ViewPager) findViewById(R.id.main_view_pager);
        MainDataFragment weekFrag = new MainDataFragment();
        weekFrag.setTag("weekFrag");
        weekFrag.setShowDateNums(7);
        MainDataFragment monthFrag = new MainDataFragment();
        monthFrag.setTag("monthFrag");
        monthFrag.setShowDateNums(30);
        MainDataFragment allFrag = new MainDataFragment();
        allFrag.setTag("allFrag");
        allFrag.setShowAllData(true);
        List<Fragment> fragList = new ArrayList<Fragment>();
        fragList.add(weekFrag);
        fragList.add(monthFrag);
        fragList.add(allFrag);
        MainDataFragmentAdapter fragmentAdapter = new MainDataFragmentAdapter(MainActivity.this.getSupportFragmentManager(), fragList);
        mViewPager.setAdapter(fragmentAdapter);
        mWeek.setTextColor(getResources().getColor(R.color.background));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                chooseFrag(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void chooseFrag(int item) {
        Drawable drawable = getResources().getDrawable(R.drawable.labselect);
        drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
        for (int i = 0; i < mSwitchLab.size(); i++) {
            if (i == item) {
                mSwitchLab.get(i).setTextColor(getResources().getColor(R.color.background));
                mSwitchLab.get(i).setCompoundDrawables(null,null,null,drawable);
            } else {
                mSwitchLab.get(i).setTextColor(getResources().getColor(R.color.main_dlg_btn_gray));
                mSwitchLab.get(i).setCompoundDrawables(null,null,null,null);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        DataManager db = DataManager.getInstance(this);
        mViewPager.setCurrentItem(0);
        chooseFrag(0);

        boolean isExOn = mSP.getBoolean(SettingsActivity.PREFERENCE_KEY_EXPORT_IMPORT, false);
        Button importButton = (Button) mNavView.getHeaderView(0).findViewById(R.id.btn_main_nav_import);
        if (isExOn) {
            importButton.setVisibility(View.VISIBLE);
            importButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ILog.d(TAG, "importButton click");
                    XMLHelper xmlHelper = new XMLHelper(MainActivity.this);
                    if (Build.VERSION.SDK_INT >= 23) {
                        int checkPermission = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
                        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, REQUSET_IMPORT_CODE_PERMISSION);
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
        Button exportButton = (Button) mNavView.getHeaderView(0).findViewById(R.id.btn_main_nav_export);
        if (isExOn) {
            exportButton.setVisibility(View.VISIBLE);
            exportButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ILog.d(TAG, "exportButton click");
                    XMLHelper helper = new XMLHelper(MainActivity.this);
                    if (Build.VERSION.SDK_INT >= 23) {
                        int checkPermission = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
                        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
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

    @Override
    protected void onStop() {
        /*this.getContentResolver().unregisterContentObserver(mObserver);*/
        super.onStop();
    }

    @Override
    protected void onStart() {
        /*this.getContentResolver().registerContentObserver(Constant.CONTENT_URI, true, mObserver);*/
        super.onStart();
    }

    /**
     * 弹出记录体重的弹框
     */
    private void showRecordDialog() {
        //builder.setTitle(R.string.)
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_main_input_weight, (ViewGroup) findViewById(R.id.dialog_layout));
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        Button btnSubmit = (Button) layout.findViewById(R.id.dialog_submit);
        final EditText editText = (EditText) layout.findViewById(R.id.dialog_input_weight);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long time = System.currentTimeMillis();
                try {
                    float weight = Float.parseFloat(editText.getText().toString());
                    if (!Utils.Companion.checkWeightValue(weight)) {
                        Toast.makeText(MainActivity.this, "您输入的值太不合理了，在逗我玩吧~", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Data data = new Data(-1, time, weight);
                    boolean isOnlyOneTime = mSP.getBoolean(SettingsActivity.PREFERENCE_KEY_ONLY_ONCE_EVERYDAY, true);
                    ILog.d(TAG, "isOnlyOnceEveryday" + isOnlyOneTime);
                    DataManager.getInstance(MainActivity.this).add(data);

                    dialog.dismiss();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "输入值不合法，请重新输入~", Toast.LENGTH_SHORT).show();
                }

            }
        });

        Button btnCancel = (Button) layout.findViewById(R.id.dialog_cacnel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View veiw) {
                dialog.dismiss();
            }
        });
        dialog.show();
        editText.requestFocus();
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (editText != null) {
                    InputMethodManager imm = (InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                }
            }
        }, 200);

    }

    /**
     * 申请存储权限完的回调方法
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        XMLHelper helper = null;
        switch (requestCode) {
            case REQUSET_EXPORT_CODE_PERMISSION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "请在“设置->应用->权限”中赋予权限后重新执行", Toast.LENGTH_SHORT).show();
                    return;
                }

                helper = new XMLHelper(this);
                helper.saveXML(mXmlListener);
                break;
            case REQUSET_IMPORT_CODE_PERMISSION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "请在“设置->应用->权限”赋予权限后重新执行", Toast.LENGTH_SHORT).show();
                    return;
                }

                helper = new XMLHelper(this);
                helper.readXML(mXmlListener);
                break;
            default:
                break;
        }
        return;
    }
}
