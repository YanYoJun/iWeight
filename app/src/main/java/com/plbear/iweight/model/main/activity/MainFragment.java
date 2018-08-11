package com.plbear.iweight.model.main.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.plbear.iweight.R;
import com.plbear.iweight.base.BaseActivity;
import com.plbear.iweight.base.BaseFragment;
import com.plbear.iweight.data.Data;
import com.plbear.iweight.data.DataManager;
import com.plbear.iweight.model.main.fragment.MainDataFragment;
import com.plbear.iweight.model.main.view.KeyboardBuilder;
import com.plbear.iweight.model.main.view.MyKeyboradView;
import com.plbear.iweight.model.other.AboutActivity;
import com.plbear.iweight.model.settings.SettingsActivity;
import com.plbear.iweight.storage.XMLHelper;
import com.plbear.iweight.utils.SPUtils;
import com.plbear.iweight.utils.Utils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;

/**
 * Created by yanyongjun on 2018/6/30.
 */

public class MainFragment extends BaseFragment {
    public final static int REQUSET_IMPORT_CODE_PERMISSION = 1;
    public final static int REQUSET_EXPORT_CODE_PERMISSION = 2;
    private XMLHelper.OnXMLListener mXmlListener;
    private ArrayList<MainDataFragment> mFragList = new ArrayList<>();
    private boolean mIsExiting = false;
    private FragmentPagerAdapter mPagerAdapter;
    private Button mBtnRecord;
    private TextView mLabNotification;
    private TabLayout mTabMain;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };


    @Override
    public int getLayout() {
        return R.layout.fragment_main;
    }

    @Override
    public void afterLayout() {
        init();
        mBtnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean onceEveryDay = SPUtils.getSP().getBoolean(SettingsActivity.PREFERENCE_KEY_ONLY_ONCE_EVERYDAY, true);
                if (onceEveryDay&&!Utils.DEBUG) {
                    String lastTime = Utils.formatTime(DataManager.getInstance().queryLastDataTime());
                    loginfo("lastTime:"+lastTime);
                    if (lastTime.equals(Utils.formatTime(System.currentTimeMillis()))) {
                        Utils.showToast(R.string.main_toast_notify_only_once);
                        return;
                    }
                }
                showChangeDialog();
            }
        });
    }

    private void init() {
        mBtnRecord = getView().findViewById(R.id.btn_record);
        mLabNotification = getView().findViewById(R.id.lab_notification);
        initLabText();
    }

    private void initLabText(){
        Data lastData = DataManager.getInstance().queryLastData();
        if(lastData == null){
            return;
        }
        String lastTime = Utils.formatTime(lastData.getTime());
        if(!lastTime.equals(Utils.formatTime(System.currentTimeMillis()))){
            mLabNotification.setText("您今日还没有打卡，要坚持哦~");
            return;
        }else{
            String str = "您最新体重是: "+lastData.getWeight()+"，继续努力哦~";
            mLabNotification.setText(str);
        }
    }

    @Override
    public void onResume() {
        //initLabText();
        super.onResume();
    }

    EditText mEditText;


    /**
     * 弹出记录体重的弹框
     */
    private void recordWeight() {
    }

    private void showChangeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        final View layout = getLayoutInflater().inflate(R.layout.dialog_main_input_weight, null);
        builder.setView(layout);

        final AlertDialog dialog = builder.create();
        Button btnSubmit = (Button) layout.findViewById(R.id.dialog_submit);
        final EditText editText = layout.findViewById(R.id.dialog_input_weight);
        editText.setText("");
        editText.setSelection(editText.getText().length());

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    float weight = Float.parseFloat(editText.getText().toString());
                    if (weight < 5 || weight > 200) {
                        Utils.showToast("您输入的值太不合理了");
                        return;
                    }
                    Data data = new Data();
                    data.setTime(System.currentTimeMillis());
                    data.setWeight(weight);
                    DataManager.getInstance().add(data);
                    dialog.dismiss();
                    initLabText();
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.showToast("请检查输入的值");
                }
            }
        });

        Button btnCancel = layout.findViewById(R.id.dialog_cacnel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    InputMethodManager input = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    input.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                }
            }
        }, 200);
    }
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        XMLHelper helper = null;
//        switch (requestCode) {
//            case REQUSET_EXPORT_CODE_PERMISSION: {
//                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(mActivity, "请在“设置->应用->权限”中赋予权限后重新执行", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                helper = new XMLHelper(mActivity);
//                helper.saveXML(mXmlListener);
//                break;
//            }
//            case REQUSET_IMPORT_CODE_PERMISSION: {
//                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(mActivity, "请在“设置->应用->权限”赋予权限后重新执行", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                helper = new XMLHelper(mActivity);
//                helper = new XMLHelper(mActivity);
//                helper.readXML(mXmlListener);
//                break;
//            }
//        }
//    }
}