package com.plbear.iweight.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.plbear.iweight.R;
import com.plbear.iweight.model.main.MainActivity;

/**
 * Created by yanyongjun on 16/11/11.
 */

public class AboutActivity extends BaseActivity {
    protected static final String TAG = "AboutActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        init();
        ImageButton btnBack = (ImageButton)findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AboutActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
    private void init(){
        super.setTag(TAG);
    }
}
