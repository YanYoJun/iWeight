package com.plbear.iweight.model.me.ui;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.plbear.iweight.R;
import com.plbear.iweight.base.BaseActivity;

public class LoginActivity extends BaseActivity {
    @Override
    public int getLayout() {
        return R.layout.activity_login;
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

        v = findViewById(R.id.btn_register);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(i);
            }
        });

        TextView labTitle = findViewById(R.id.lab_title);
        labTitle.setText("登录");
    }
}
