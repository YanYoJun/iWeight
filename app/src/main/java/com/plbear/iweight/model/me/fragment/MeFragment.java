package com.plbear.iweight.model.me.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import com.plbear.iweight.model.other.AboutActivity;
import com.plbear.iweight.model.settings.SettingsActivity;
import com.plbear.iweight.utils.SPUtils;
import com.plbear.iweight.utils.Utils;

import java.util.Timer;
import java.util.TimerTask;

public class MeFragment extends BaseFragment {
    @Override
    public int getLayout() {
        return R.layout.fragment_me;
    }

    @Override
    public void afterLayout() {
        final Switch swich = mActivity.findViewById(R.id.switch_once);
        swich.setClickable(false);
        swich.setChecked(SPUtils.getSP().getBoolean(Constant.PREFERENCE_KEY_ONLY_ONCE_EVERYDAY, true));

        notifyChange();

        View v = mActivity.findViewById(R.id.view_about);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mActivity, AboutActivity.class);
                mActivity.startActivity(i);
            }
        });

        v = mActivity.findViewById(R.id.view_wechat);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
            }
        });

        v = mActivity.findViewById(R.id.view_once);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean curValue = SPUtils.getSP().getBoolean(Constant.PREFERENCE_KEY_ONLY_ONCE_EVERYDAY, true);
                swich.setChecked(!curValue);
                SPUtils.save(Constant.PREFERENCE_KEY_ONLY_ONCE_EVERYDAY, !curValue);
            }
        });

        v = mActivity.findViewById(R.id.view_target);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSetTargetDialog();
            }
        });

        v = mActivity.findViewById(R.id.view_unit);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUnitDialog();
            }
        });

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
        float curValues = SPUtils.getSP().getFloat(SettingsActivity.PREFERENCE_KEY_SET_TARGET_WEIGHT, 0f);
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
                    SPUtils.save(SettingsActivity.PREFERENCE_KEY_SET_TARGET_WEIGHT, weight);
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


//    private void initNav() {
//        View btnAbout = mNavView.getHeaderView(0).findViewById(R.id.btn_main_nav_about);
//        btnAbout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(mActivity, AboutActivity.class);
//                startActivity(i);
//            }
//        });
//
//        View exitButton = mNavView.getHeaderView(0).findViewById(R.id.btn_main_nav_exit);
//        exitButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mActivity.exitAll();
//            }
//        });
//
//        View detailsButton = mNavView.getHeaderView(0).findViewById(R.id.btn_main_nav_detail);
//        detailsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(mActivity, DetailsActivity.class);
//                startActivity(i);
//            }
//        });
//
//        View settingsButton = mNavView.getHeaderView(0).findViewById(R.id.btn_main_nav_settings);
//        settingsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(mActivity, SettingsActivity.class);
//                startActivity(i);
//            }
//        });
//    }

//    boolean isExOn = SPUtils.getSP().getBoolean(SettingsActivity.PREFERENCE_KEY_EXPORT_IMPORT, false);
//    Button importButton = mNavView.getHeaderView(0).findViewById(R.id.btn_main_nav_import);
//        if (isExOn) {
//                importButton.setVisibility(View.VISIBLE);
//                importButton.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View v) {
//        XMLHelper xmlHelper = new XMLHelper(mActivity);
//        if (Build.VERSION.SDK_INT >= 23) {
//        int checkPermission = ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE);
//        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
//        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS},
//        REQUSET_IMPORT_CODE_PERMISSION);
//        return;
//        }
//        }
//        xmlHelper.readXML(mXmlListener);
//        }
//        });
//        } else {
//        importButton.setVisibility(View.GONE);
//        }
//
//
//        /**
//         * 导出
//         */
//        Button exportButton = mNavView.getHeaderView(0).findViewById(R.id.btn_main_nav_export);
//        if (isExOn) {
//        exportButton.setVisibility(View.VISIBLE);
//        exportButton.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View v) {
//        XMLHelper helper = new XMLHelper(mActivity);
//        if (Build.VERSION.SDK_INT >= 23) {
//        int checkPermission = ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE);
//        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
//        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
//        Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, REQUSET_EXPORT_CODE_PERMISSION);
//        return;
//        }
//        }
//        helper.saveXML(mXmlListener);
//        }
//        });
//        } else {
//        exportButton.setVisibility(View.GONE);
//        }