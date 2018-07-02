package com.plbear.iweight.model.settings;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.plbear.iweight.R;
import com.plbear.iweight.base.BaseActivity;

import butterknife.BindView;

/**
 * Created by yanyongjun on 2018/6/11.
 */

public class SettingsActivity extends BaseActivity {
    @BindView(R.id.lab_title)
    TextView lab_title;

    @BindView(R.id.btn_details_select_all)
    Button btn_details_select_all;

    @Override
    public int getLayout() {
        return R.layout.activity_settings;
    }

    @Override
    public void afterLayout() {
        init();
    }

    public void init(){
        lab_title.setText(R.string.settings);
        btn_details_select_all.setVisibility(View.GONE);
    }



    public static String PREFERENCE_KEY_SET_TARGET_WEIGHT = "set_target_weight";
    public static String PREFERENCE_KEY_ONLY_ONCE_EVERYDAY = "only_once_everyday";
    public static String PREFERENCE_KEY_EXPORT_IMPORT = "export_import_switch";
    public static String PREFERENCE_KEY_UNIT = "value_unit";
}
