package com.plbear.iweight.model.me.ui;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.plbear.iweight.R;
import com.plbear.iweight.base.BaseActivity;
import com.plbear.iweight.base.Constant;
import com.plbear.iweight.http.Bean.User;
import com.plbear.iweight.http.HttpPost;
import com.plbear.iweight.utils.SPUtils;
import com.plbear.iweight.utils.Utils;

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

        final TextView labNotify = findViewById(R.id.lab_notify);
        labNotify.setVisibility(View.INVISIBLE);


        final EditText editName = findViewById(R.id.edit_name);
        final EditText editPasswd = findViewById(R.id.edit_passwd);
        final EditText editPasswdDouble = findViewById(R.id.edit_passwd_double);

        final View btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editName.getText().toString();
                String passwd = editPasswd.getText().toString();
                String passwdDouble = editPasswdDouble.getText().toString();

                if(TextUtils.isEmpty(name) || TextUtils.isEmpty(passwd) || TextUtils.isEmpty(passwdDouble)){
                    labNotify.setVisibility(View.VISIBLE);
                    labNotify.setText("请输入用户名和密码");
                    return;
                }

                if(!passwd.equals(passwdDouble)){
                    labNotify.setVisibility(View.VISIBLE);
                    labNotify.setText("两次密码不一致");
                    return;
                }

                final User user = new User();
                user.setName(name);
                user.setPasswd(passwd);

                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected void onPostExecute(Boolean aVoid) {
                        if (aVoid) {
                            Utils.showToast("注册成功");
                            SPUtils.save(Constant.PRE_KEY_LOGIN_NAME, user.getName());
                            SPUtils.save(Constant.PRE_KEY_LOGIN_PASSWD, user.getPasswd());
                            SPUtils.save(Constant.PRE_KEY_USER_ID, user.getUserid());
                            finish();
                        } else {
                            SPUtils.save(Constant.PRE_KEY_LOGIN_STATUS, false);
                            Utils.showToast("注册失败，请检查网络");
                        }
                        super.onPostExecute(aVoid);
                    }

                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        HttpPost httpPost = HttpPost.getInstance();
                        return httpPost.create(user);
                    }
                }.execute();
            }
        });



    }
}
