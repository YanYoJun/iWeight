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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import com.plbear.iweight.R
import com.plbear.iweight.utils.MyLog
import com.plbear.iweight.utils.MyLog.Companion
import com.plbear.iweight.utils.SPUtils
import com.plbear.iweight.utils.Utils
import com.plbear.iweight.model.main.MainDataFragment

import java.util.Timer
import java.util.TimerTask

import android.content.ContentValues.TAG

/**
 * Created by yanyongjun on 2017/4/1.
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
        MyLog.i(TAG, "onPreferenceTreeclick:" + preference.key)
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
        mSP = SPUtils.getSp()
        mSPEditor = mSP!!.edit()
    }

    override fun onResume() {
        val valUnitWeight = findPreference(SettingsActivity.PREFERENCE_KEY_UNIT) as ListPreference
        val value = mSP!!.getString(SettingsActivity.PREFERENCE_KEY_UNIT, "1")
        initUnitPreference(value)
        valUnitWeight.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
            Utils.clearValueUnit()
            initUnitPreference(newValue as String)
            val intent = Intent(MainDataFragment.ACTION_DATA_CHANED)
            activity.sendBroadcast(intent)
            true
        }
        super.onResume()
    }

    private fun initUnitPreference(value: String) {
        val valUnitWeight = findPreference(SettingsActivity.PREFERENCE_KEY_UNIT) as ListPreference

        if (value == "1") {
            valUnitWeight.summary = String.format(getString(R.string.current_unit), "公斤")
        } else if (value == "2") {
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
        val layout = inflater.inflate(R.layout.dialog_main_input_weight, mContext!!.findViewById<View>(R.id.dialog_layout) as ViewGroup)
        val labTitle = layout.findViewById<View>(R.id.dialog_lab_title) as TextView
        labTitle.setText(R.string.dialog_target_widget_title)
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
                    Toast.makeText(mContext, "您输入的值太不合理了，在逗我玩吧~", Toast.LENGTH_SHORT).show()
                    return@OnClickListener
                }
                savePreferences(SettingsActivity.PREFERENCE_KEY_SET_TARGET_WEIGHT, weight)
                Toast.makeText(mContext, R.string.save_target_weight_success, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } catch (e: Exception) {
                Toast.makeText(mContext, "输入值不合法，请重新输入~", Toast.LENGTH_SHORT).show()
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
