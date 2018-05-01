package com.plbear.iweight.model.other;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.plbear.iweight.R;
import com.plbear.iweight.base.BaseActivity;
import com.plbear.iweight.model.main.activity.MainActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by yanyongjun on 2018/5/1.
 */

public class AboutJActivity extends BaseActivity{
    @BindView(R.id.btn_details_select_all) Button mBtnSelectAll;

    @Override
    public int getLayout() {
        return R.layout.activity_about;
    }

    @Override
    public void afterLayout() {
        mBtnSelectAll.setVisibility(View.GONE);
    }

    @OnClick(R.id.btn_back)
    public void onClick_back(View v){
        logerror("onCick_back");
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

}
