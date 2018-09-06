package com.plbear.iweight.model.main.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.plbear.iweight.R;
import com.plbear.iweight.base.BaseFragment;
import com.plbear.iweight.base.Constant;
import com.plbear.iweight.data.Data;
import com.plbear.iweight.data.DataManager;
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

    @BindView(R.id.btn_record)
    Button mBtnRecord;

    @BindView(R.id.lab_notification)
    TextView mLabNotification;


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
                boolean onceEveryDay = SPUtils.getSP().getBoolean(Constant.PREFERENCE_KEY_ONLY_ONCE_EVERYDAY, false);
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
}