package com.plbear.iweight.model.me.ui;

import android.view.View;
import android.widget.TextView;

import com.plbear.iweight.R;
import com.plbear.iweight.base.BaseActivity;

import butterknife.OnClick;

public class RegisterActivity extends BaseActivity {
    @Override
    public int getLayout() {
        return R.layout.activity_register;
    }

    @Override
    public void afterLayout() {
        View v = findViewById(R.id.btn_back);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView labTitle = findViewById(R.id.lab_title);
        labTitle.setText("注册");
    }
}
