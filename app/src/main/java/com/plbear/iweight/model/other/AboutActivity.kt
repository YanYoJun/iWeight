package com.plbear.iweight.model.other

import android.content.Intent
import android.view.View

import com.plbear.iweight.R
import com.plbear.iweight.base.BaseActivity
import com.plbear.iweight.model.main.activity.MainActivity
import kotlinx.android.synthetic.main.include_title.*

/**
 * Created by yanyongjun on 16/11/11.
 */

class AboutActivity : BaseActivity() {
    override fun getLayout(): Int {
        return R.layout.activity_about
    }

    override fun afterLayout() {

    }

    fun onClick_back(v: View) {
        btn_back.setOnClickListener {
            val intent = Intent(this@AboutActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
