package com.plbear.iweight.model.me.ui;

import android.view.View;

import com.plbear.iweight.R;
import com.plbear.iweight.base.BaseActivity;

/**
 * Created by yanyongjun on 2018/5/1.
 */

public class AboutActivity extends BaseActivity{

    @Override
    public int getLayout() {
        return R.layout.activity_about;
    }

    @Override
    public void afterLayout() {
        View back = findViewById(R.id.btn_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
