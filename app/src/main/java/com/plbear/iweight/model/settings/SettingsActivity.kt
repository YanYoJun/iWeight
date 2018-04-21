package com.plbear.iweight.model.settings

import android.view.View

import com.plbear.iweight.R
import com.plbear.iweight.base.BaseActivity
import kotlinx.android.synthetic.main.include_title.*

/**
 * Created by HuHu on 2017/normal_3/13.
 */

class SettingsActivity : BaseActivity() {

    override fun getLayout(): Int {
        return R.layout.activity_settings
    }

    override fun afterLayout() {
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
        public val PREFERENCE_KEY_UNIT = "value_unit"

        private val TAG = "SettingsActivity"
    }
}
