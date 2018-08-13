package com.plbear.iweight.model.me.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.plbear.iweight.R;
import com.plbear.iweight.base.BaseActivity;
import com.plbear.iweight.base.Constant;
import com.plbear.iweight.data.NetworkDataManager;
import com.plbear.iweight.http.Bean.User;
import com.plbear.iweight.http.HttpPost;
import com.plbear.iweight.utils.SPUtils;
import com.plbear.iweight.utils.Utils;

public class LoginActivity extends BaseActivity {
    private EditText mEditName;
    private EditText mEditPasswd;

    @Override
    public int getLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        TextView labTitle = findViewById(R.id.lab_title);
        labTitle.setText("登录");

        mEditName = findViewById(R.id.edit_name);
        mEditPasswd = findViewById(R.id.edit_passwd);

        mEditName.setText(SPUtils.getSP().getString(Constant.PRE_KEY_LOGIN_NAME,""));
        mEditPasswd.setText(SPUtils.getSP().getString(Constant.PRE_KEY_LOGIN_PASSWD,""));


        v = findViewById(R.id.btn_login);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = mEditName.getText().toString();
                String passwd = mEditPasswd.getText().toString();
                if (name == null || passwd == null || name.length() == 0 || passwd.length() == 0) {
                    Utils.showToast("请输入用户名和密码");
                    return;
                }
                final User user = new User();
                user.setName(name);
                user.setPasswd(passwd);

                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected void onPostExecute(Boolean aVoid) {
                        if (aVoid) {
                            Utils.showToast("登录成功");
                            SPUtils.save(Constant.PRE_KEY_LOGIN_NAME, user.getName());
                            SPUtils.save(Constant.PRE_KEY_LOGIN_PASSWD, user.getPasswd());
                            SPUtils.save(Constant.PRE_KEY_USER_ID, user.getUserid());
                            SPUtils.save(Constant.PRE_KEY_LOGIN_STATUS, true);
                            NetworkDataManager.getsInstance().sync();
                            finish();
                        } else {
                            SPUtils.save(Constant.PRE_KEY_LOGIN_STATUS, false);
                            Utils.showToast("登录失败，请检查网络");
                        }
                        super.onPostExecute(aVoid);
                    }

                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        HttpPost httpPost = HttpPost.getInstance();
                        return httpPost.login(user);
                    }
                }.execute();
            }
        });


    }
}
