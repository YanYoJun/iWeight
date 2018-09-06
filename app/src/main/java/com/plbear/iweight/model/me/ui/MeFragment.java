package com.plbear.iweight.model.me.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.UniversalTimeScale;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.plbear.iweight.base.BaseFragment;
import com.plbear.iweight.R;
import com.plbear.iweight.base.Constant;
import com.plbear.iweight.utils.SPUtils;
import com.plbear.iweight.utils.Utils;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;

public class MeFragment extends BaseFragment {

    @BindView(R.id.lab_login_title) TextView mLabLoginTitle;
    @BindView(R.id.lab_login_notify) TextView mLabLoginNotify;

    @BindView(R.id.switch_once) Switch mSwitchOnce;
    @BindView(R.id.view_about) View mViewAbout;
    @BindView(R.id.view_wechat) View mViewWeChat;
    @BindView(R.id.view_once) View mViewOnce;
    @BindView(R.id.view_target) View mViewTarget;
    @BindView(R.id.view_unit) View mViewUnit;
    @BindView(R.id.view_quit) View mViewQuit;

    @Override
    public int getLayout() {
        return R.layout.fragment_me;
    }

    @Override
    public void afterLayout() {
        mSwitchOnce.setClickable(false);
        mSwitchOnce.setChecked(SPUtils.getSP().getBoolean(Constant.PREFERENCE_KEY_ONLY_ONCE_EVERYDAY, false));

        notifyChange();

        mViewAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mActivity, AboutActivity.class);
                mActivity.startActivity(i);
            }
        });

        mViewWeChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean status = SPUtils.getSP().getBoolean(Constant.PRE_KEY_LOGIN_STATUS, false);
                if (status) {
                    Utils.showToast("已登录");
                } else {
                    Intent i = new Intent(mActivity, LoginActivity.class);
                    mActivity.startActivity(i);
                }
            }
        });

        mViewOnce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean curValue = SPUtils.getSP().getBoolean(Constant.PREFERENCE_KEY_ONLY_ONCE_EVERYDAY, false);
                mSwitchOnce.setChecked(!curValue);
                SPUtils.save(Constant.PREFERENCE_KEY_ONLY_ONCE_EVERYDAY, !curValue);
            }
        });

        mViewTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSetTargetDialog();
            }
        });

        mViewUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUnitDialog();
            }
        });

        mViewQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SPUtils.save(Constant.PRE_KEY_LOGIN_STATUS, false);
                initLoginLab();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initLoginLab();
    }

    private void initLoginLab() {
        if (SPUtils.getSP().getBoolean(Constant.PRE_KEY_LOGIN_STATUS, false)) {
            mLabLoginNotify.setText("已登录，将自动备份数据");
            mLabLoginTitle.setText(SPUtils.getSP().getString(Constant.PRE_KEY_LOGIN_NAME, ""));
        } else {
            mLabLoginNotify.setText("未登录，登录后将自动备份数据");
            mLabLoginTitle.setText("登录、注册");
        }
    }

    private void notifyChange() {
        final TextView labWeight = mActivity.findViewById(R.id.lab_target);
        if (SPUtils.getSP().getFloat(Constant.PREFERENCE_KEY_SET_TARGET_WEIGHT, -1) != -1) {
            labWeight.setText(SPUtils.getSP().getFloat(Constant.PREFERENCE_KEY_SET_TARGET_WEIGHT, -1) + " 公斤");
        }

        final TextView labUnit = mActivity.findViewById(R.id.lab_unit);
        float unit = SPUtils.getSP().getFloat(Constant.PREFERENCE_KEY_UNIT, 1);
        if (unit == 1) {
            labUnit.setText("公斤");
        } else {
            labUnit.setText("斤");
        }
        Utils.clearValueUnit();

    }


    /**
     * pop set target dialog
     */
    private void showSetTargetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_main_input_weight, null);
        TextView labTitle = (TextView) layout.findViewById(R.id.dialog_lab_title);
        labTitle.setText(R.string.settings_dialog_target_title);
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        Button btnSubmit = layout.findViewById(R.id.dialog_submit);
        final EditText editText = layout.findViewById(R.id.dialog_input_weight);
        editText.setHint("请输入公斤制体重，切记切记");

        //set default values
        float curValues = SPUtils.getSP().getFloat(Constant.PREFERENCE_KEY_SET_TARGET_WEIGHT, 0f);
        if (curValues != 0f) {
            editText.setText(curValues + "");
        }
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long time = System.currentTimeMillis();
                try {
                    float weight = Float.parseFloat(editText.getText().toString());
                    if (!Utils.checkWeightValue(weight)) {
                        Utils.showToast("您输入的值太不合理了，在逗我玩吧~");
                        return;
                    }
                    SPUtils.save(Constant.PREFERENCE_KEY_SET_TARGET_WEIGHT, weight);
                    Utils.showToast(R.string.settings_toast_save_target_success);

                    notifyChange();
                    dialog.dismiss();
                } catch (Exception e) {
                    Utils.showToast("输入值不合法，请重新输入~");
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
                    InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                }
            }
        }, 200);
    }


    private void showUnitDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_input_unit, null);
        builder.setView(layout);
        final AlertDialog dialog = builder.create();

        final RadioGroup group = layout.findViewById(R.id.radio_group_unit);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int checkBtn = group.getCheckedRadioButtonId();
                switch (checkBtn) {
                    case R.id.radBtn_jin:
                        SPUtils.save(Constant.PREFERENCE_KEY_UNIT, 2.0f);
                        break;
                    case R.id.radBtn_gongjin:
                        SPUtils.save(Constant.PREFERENCE_KEY_UNIT, 1.0f);
                        break;
                }

                notifyChange();
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}