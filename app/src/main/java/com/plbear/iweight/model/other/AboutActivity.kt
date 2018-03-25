package com.plbear.iweight.model.other

import android.content.Intent
import android.view.View
import android.widget.ImageButton

import com.plbear.iweight.R
import com.plbear.iweight.activity.BaseActivity
import com.plbear.iweight.model.main.MainActivity

/**
 * Created by yanyongjun on 16/11/11.
 */

class AboutActivity : BaseActivity() {
    override fun getLayout(): Int {
        return R.layout.activity_about
    }

    override fun afterLayout() {
        val btnBack = findViewById<View>(R.id.btn_back) as ImageButton
        btnBack.setOnClickListener {
            val intent = Intent(this@AboutActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
