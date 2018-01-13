package com.plbear.iweight.model.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.plbear.iweight.R;
import com.plbear.iweight.Utils.ILog;
import com.plbear.iweight.activity.BaseActivity;
import com.plbear.iweight.model.main.MainActivity;

/**
 * Created by HuHu on 2017/3/13.
 */

public class SettingsActivity extends BaseActivity {
    public static final String PREFERENCE_KEY_SET_TARGET_WEIGHT = "set_target_weight";
    public static final String PREFERENCE_KEY_ONLY_ONCE_EVERYDAY = "only_once_everyday";
    public static final String PREFERENCE_KEY_EXPORT_IMPORT = "export_import_switch";
    public static final String PREFERENCE_KEY_UNIT = "value_unit";

    private static final String TAG = "SettingsActivity";

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ILog.d(TAG, "onCreate");
        init();
        ImageButton btnBack = (ImageButton) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        /*getFragmentManager().beginTransaction().replace(R.id.settings_fragment,new SettingsFragment()).commit();*/
        /*addPreferencesFromResource(R.xml.settings_preferences);*/
    }

    private void init() {

        TextView title = (TextView) findViewById(R.id.lab_title);
        title.setText(R.string.settings);
    }
}
