package com.plbear.iweight.model.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView

import com.plbear.iweight.R
import com.plbear.iweight.utils.MyLog
import com.plbear.iweight.utils.MyLog.Companion
import com.plbear.iweight.activity.BaseActivity
import com.plbear.iweight.model.main.MainActivity
import kotlinx.android.synthetic.main.include_title.*

/**
 * Created by HuHu on 2017/3/13.
 */

class SettingsActivity : BaseActivity() {

    override fun getLayout(): Int {
        return R.layout.activity_settings
    }

    override fun afterLayout() {
        MyLog.d(TAG, "onCreate")
        init()
    }

    fun onClick_back(v:View){
        finish()
    }

    private fun init() {
        lab_title.setText(R.string.settings)
    }

    companion object {
        val PREFERENCE_KEY_SET_TARGET_WEIGHT = "set_target_weight"
        val PREFERENCE_KEY_ONLY_ONCE_EVERYDAY = "only_once_everyday"
        val PREFERENCE_KEY_EXPORT_IMPORT = "export_import_switch"
        val PREFERENCE_KEY_UNIT = "value_unit"

        private val TAG = "SettingsActivity"
    }
}
