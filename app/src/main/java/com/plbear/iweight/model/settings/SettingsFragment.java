package com.plbear.iweight.model.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.plbear.iweight.R;
import com.plbear.iweight.utils.LogInfo;
import com.plbear.iweight.utils.Utils;
import com.plbear.iweight.model.form.ui.FormViewFrag;

import java.util.Timer;;
import java.util.TimerTask;

import com.plbear.iweight.utils.SPUtils;

/**
 * Created by yanyongjun on 2017/normal_4/normal_1.
 */

public class SettingsFragment extends PreferenceFragment {
    private final static String TAG = "SettingsFragment";
    private Activity mContext = null;
    private SharedPreferences mSP = null;
    private SharedPreferences.Editor mSPEditor = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_preferences);
        init();
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();

        if (key.equals(SettingsActivity.PREFERENCE_KEY_SET_TARGET_WEIGHT)) {
            showSetTargetDialog();
        } else if (key.equals(SettingsActivity.PREFERENCE_KEY_ONLY_ONCE_EVERYDAY)) {
            SwitchPreference swPre = (SwitchPreference) preference;
            mSPEditor.putBoolean(SettingsActivity.PREFERENCE_KEY_ONLY_ONCE_EVERYDAY, swPre.isChecked());
            mSPEditor.commit();
        } else if (key.equals(SettingsActivity.PREFERENCE_KEY_EXPORT_IMPORT)) {
            SwitchPreference swEx = (SwitchPreference) preference;
            mSPEditor.putBoolean(SettingsActivity.PREFERENCE_KEY_EXPORT_IMPORT, swEx.isChecked());
            mSPEditor.commit();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }


    private void init() {
        mContext = getActivity();
        mSP = SPUtils.getSP();
        mSPEditor = mSP.edit();
    }

    @Override
    public void onResume() {
        ListPreference valUnitWeight = (ListPreference) findPreference(SettingsActivity.PREFERENCE_KEY_UNIT);
        String value = mSP.getString(SettingsActivity.PREFERENCE_KEY_UNIT, "1");
        initUnitPreference(value);
        LogInfo.i(TAG,"onResume："+value);
        valUnitWeight.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Utils.clearValueUnit();
                initUnitPreference((String) newValue);
                Intent intent = new Intent(FormViewFrag.ACTION_DATA_CHANED);
                getActivity().sendBroadcast(intent);
                return true;
            }
        });

        super.onResume();
    }

    private void initUnitPreference(String value) {
        ListPreference valUnitWeight = (ListPreference) findPreference(SettingsActivity.PREFERENCE_KEY_UNIT);

        if (value.equals("1")) {
            valUnitWeight.setSummary(String.format(getString(R.string.current_unit), "公斤"));
        } else if (value.equals("2")) {
            valUnitWeight.setSummary(String.format(getString(R.string.current_unit), "斤"));
        }
    }

    private void savePreferences(String key, int values) {
        mSPEditor.putInt(key, values);
        mSPEditor.commit();
    }

    private void savePreferences(String key, float values) {
        mSPEditor.putFloat(key, values);
        mSPEditor.commit();
    }

    /**
     * pop set target dialog
     */
    private void showSetTargetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = mContext.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_main_input_weight, null);
        TextView labTitle = (TextView) layout.findViewById(R.id.dialog_lab_title);
        labTitle.setText(R.string.settings_dialog_target_title);
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        Button btnSubmit = layout.findViewById(R.id.dialog_submit);
        final EditText editText = layout.findViewById(R.id.dialog_input_weight);
        editText.setHint("请输入公斤制体重，切记切记");

        //set default values
        float curValues = mSP.getFloat(SettingsActivity.PREFERENCE_KEY_SET_TARGET_WEIGHT, 0f);
        if (curValues != 0f) {
            editText.setText(curValues + "");
        }
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long time = System.currentTimeMillis();
                try {
                    long weight = Long.parseLong(editText.getText().toString());
                    if (!Utils.checkWeightValue(weight)) {
                        Utils.showToast("您输入的值太不合理了，在逗我玩吧~");
                        return;
                    }
                    savePreferences(SettingsActivity.PREFERENCE_KEY_SET_TARGET_WEIGHT, weight);
                    Utils.showToast(R.string.settings_toast_save_target_success);
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
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                }
            }
        }, 200);

    }
}
