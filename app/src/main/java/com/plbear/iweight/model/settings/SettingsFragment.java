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
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.plbear.iweight.R;
import com.plbear.iweight.Utils.ILog;
import com.plbear.iweight.Utils.OldUtils;
import com.plbear.iweight.Utils.SPUtils;
import com.plbear.iweight.Utils.Utils;
import com.plbear.iweight.model.main.MainDataFragment;
import com.plbear.iweight.model.settings.SettingsActivity;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

/**
 * Created by yanyongjun on 2017/4/1.
 */

public class SettingsFragment extends PreferenceFragment {
    private Activity mContext = null;
    private SharedPreferences mSP = null;
    private SharedPreferences.Editor mSPEditor = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_preferences);
        init();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        ILog.i(TAG, "onPreferenceTreeclick:" + preference.getKey());
        String key = preference.getKey();
        switch (key) {
            case SettingsActivity.PREFERENCE_KEY_SET_TARGET_WEIGHT:
                showSetTargetDialog();
                break;
            case SettingsActivity.PREFERENCE_KEY_ONLY_ONCE_EVERYDAY:
                SwitchPreference swPre = (SwitchPreference) preference;
                mSPEditor.putBoolean(SettingsActivity.PREFERENCE_KEY_ONLY_ONCE_EVERYDAY, swPre.isChecked());
                mSPEditor.commit();
                break;
            case SettingsActivity.PREFERENCE_KEY_EXPORT_IMPORT:
                SwitchPreference swEx = (SwitchPreference) preference;
                mSPEditor.putBoolean(SettingsActivity.PREFERENCE_KEY_EXPORT_IMPORT, swEx.isChecked());
                mSPEditor.commit();
            default:
                break;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }


    private void init() {
        mContext = getActivity();
        mSP = SPUtils.Companion.getSP(getActivity());
        mSPEditor = mSP.edit();
    }

    @Override
    public void onResume() {
        ListPreference valUnitWeight = (ListPreference) findPreference(SettingsActivity.PREFERENCE_KEY_UNIT);
        String value = mSP.getString(SettingsActivity.PREFERENCE_KEY_UNIT, "1");
        initUnitPreference(value);
        valUnitWeight.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                OldUtils.clearValueUnit();
                initUnitPreference((String) newValue);
                Intent intent = new Intent(MainDataFragment.ACTION_DATA_CHANED);
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
        View layout = inflater.inflate(R.layout.dialog_main_input_weight, (ViewGroup) mContext.findViewById(R.id.dialog_layout));
        TextView labTitle = (TextView) layout.findViewById(R.id.dialog_lab_title);
        labTitle.setText(R.string.dialog_target_widget_title);
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        Button btnSubmit = (Button) layout.findViewById(R.id.dialog_submit);
        final EditText editText = (EditText) layout.findViewById(R.id.dialog_input_weight);
        editText.setHint("请输入公斤制体重，切记切记");

        //set default values
        float curValues = mSP.getFloat(SettingsActivity.PREFERENCE_KEY_SET_TARGET_WEIGHT, 0);
        if (curValues != 0) {
            editText.setText(curValues + "");
        }
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long time = System.currentTimeMillis();
                try {
                    float weight = Float.parseFloat(editText.getText().toString());
                    if (!Utils.Companion.checkWeightValue(weight)) {
                        Toast.makeText(mContext, "您输入的值太不合理了，在逗我玩吧~", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    savePreferences(SettingsActivity.PREFERENCE_KEY_SET_TARGET_WEIGHT, weight);
                    Toast.makeText(mContext, R.string.save_target_weight_success, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } catch (Exception e) {
                    Toast.makeText(mContext, "输入值不合法，请重新输入~", Toast.LENGTH_SHORT).show();
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
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                }
            }
        }, 200);

    }
}
