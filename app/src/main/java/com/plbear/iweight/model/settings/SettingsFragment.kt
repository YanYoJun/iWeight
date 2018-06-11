package com.plbear.iweight.model.settings

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceScreen
import android.preference.SwitchPreference
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

import com.plbear.iweight.R
import com.plbear.iweight.utils.Utils
import com.plbear.iweight.model.main.fragment.MainDataFragment

import java.util.Timer
import java.util.TimerTask

import com.plbear.iweight.utils.SPUtils
import com.plbear.iweight.utils.MyUtils

/**
 * Created by yanyongjun on 2017/normal_4/normal_1.
 */

class SettingsFragment : PreferenceFragment() {
    private var mContext: Activity? = null
    private var mSP: SharedPreferences? = null
    private var mSPEditor: SharedPreferences.Editor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings_preferences)
        init()
    }

    override fun onPreferenceTreeClick(preferenceScreen: PreferenceScreen, preference: Preference): Boolean {
        val key = preference.key
        when (key) {
            SettingsActivity.PREFERENCE_KEY_SET_TARGET_WEIGHT -> showSetTargetDialog()
            SettingsActivity.PREFERENCE_KEY_ONLY_ONCE_EVERYDAY -> {
                val swPre = preference as SwitchPreference
                mSPEditor!!.putBoolean(SettingsActivity.PREFERENCE_KEY_ONLY_ONCE_EVERYDAY, swPre.isChecked)
                mSPEditor!!.commit()
            }
            SettingsActivity.PREFERENCE_KEY_EXPORT_IMPORT -> {
                val swEx = preference as SwitchPreference
                mSPEditor!!.putBoolean(SettingsActivity.PREFERENCE_KEY_EXPORT_IMPORT, swEx.isChecked)
                mSPEditor!!.commit()
            }
            else -> {
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference)
    }


    private fun init() {
        mContext = activity
        mSP = SPUtils.getSP()
        mSPEditor = mSP!!.edit()
    }

    override fun onResume() {
        val valUnitWeight = findPreference(SettingsActivity.PREFERENCE_KEY_UNIT) as ListPreference
        val value = mSP!!.getString(SettingsActivity.PREFERENCE_KEY_UNIT, "normal_1")
        initUnitPreference(value)
        valUnitWeight.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
            MyUtils.clearValueUnit()
            initUnitPreference(newValue as String)
            val intent = Intent(MainDataFragment.ACTION_DATA_CHANED)
            activity.sendBroadcast(intent)
            true
        }
        super.onResume()
    }

    private fun initUnitPreference(value: String) {
        val valUnitWeight = findPreference(SettingsActivity.PREFERENCE_KEY_UNIT) as ListPreference

        if (value == "normal_1") {
            valUnitWeight.summary = String.format(getString(R.string.current_unit), "公斤")
        } else if (value == "normal_2") {
            valUnitWeight.summary = String.format(getString(R.string.current_unit), "斤")
        }
    }

    private fun savePreferences(key: String, values: Int) {
        mSPEditor!!.putInt(key, values)
        mSPEditor!!.commit()
    }

    private fun savePreferences(key: String, values: Float) {
        mSPEditor!!.putFloat(key, values)
        mSPEditor!!.commit()
    }

    /**
     * pop set target dialog
     */
    private fun showSetTargetDialog() {
        val builder = AlertDialog.Builder(mContext)
        val inflater = mContext!!.layoutInflater
        val layout = inflater.inflate(R.layout.dialog_main_input_weight, null)
        val labTitle = layout.findViewById<View>(R.id.dialog_lab_title) as TextView
        labTitle.setText(R.string.settings_dialog_target_title)
        builder.setView(layout)
        val dialog = builder.create()
        val btnSubmit = layout.findViewById<View>(R.id.dialog_submit) as Button
        val editText = layout.findViewById<View>(R.id.dialog_input_weight) as EditText
        editText.hint = "请输入公斤制体重，切记切记"

        //set default values
        val curValues = mSP!!.getFloat(SettingsActivity.PREFERENCE_KEY_SET_TARGET_WEIGHT, 0f)
        if (curValues != 0f) {
            editText.setText(curValues.toString() + "")
        }
        btnSubmit.setOnClickListener(View.OnClickListener {
            val time = System.currentTimeMillis()
            try {
                val weight = java.lang.Float.parseFloat(editText.text.toString())
                if (!Utils.checkWeightValue(weight)) {
                    Utils.showToast("您输入的值太不合理了，在逗我玩吧~")
                    return@OnClickListener
                }
                savePreferences(SettingsActivity.PREFERENCE_KEY_SET_TARGET_WEIGHT, weight)
                Utils.showToast(R.string.settings_toast_save_target_success)
                dialog.dismiss()
            } catch (e: Exception) {
                Utils.showToast("输入值不合法，请重新输入~")
            }
        })

        val btnCancel = layout.findViewById<View>(R.id.dialog_cacnel) as Button
        btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
        editText.requestFocus()
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (editText != null) {
                    val imm = mContext!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED)
                }
            }
        }, 200)

    }
}
