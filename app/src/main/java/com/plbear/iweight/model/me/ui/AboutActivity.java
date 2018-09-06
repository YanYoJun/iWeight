package com.plbear.iweight.model.me.ui;

import android.view.View;
import android.widget.Button;

import com.plbear.iweight.R;
import com.plbear.iweight.base.BaseActivity;

import butterknife.BindView;

/**
 * Created by yanyongjun on 2018/5/1.
 */

public class AboutActivity extends BaseActivity{

    @BindView(R.id.btn_back)
    View mBtnBack;

    @Override
    public int getLayout() {
        return R.layout.activity_about;
    }

    @Override
    public void afterLayout() {
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
