package com.plbear.iweight.model.me.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.plbear.iweight.R;
import com.plbear.iweight.base.BaseActivity;
import com.plbear.iweight.base.Constant;
import com.plbear.iweight.data.NetworkDataManager;
import com.plbear.iweight.http.Bean.User;
import com.plbear.iweight.http.HttpPost;
import com.plbear.iweight.utils.SPUtils;
import com.plbear.iweight.utils.ThreadUtils;
import com.plbear.iweight.utils.Utils;

import butterknife.BindView;

public class LoginActivity extends BaseActivity {
    @BindView(R.id.edit_name) EditText mEditName;
    @BindView(R.id.edit_passwd) EditText mEditPasswd;
    @BindView(R.id.lab_title) TextView mLabTitle;
    @BindView(R.id.btn_back) View mBtnBack;
    @BindView(R.id.btn_register) Button mBtnRegister;
    @BindView(R.id.btn_login) Button mBtnLogin;


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
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        mLabTitle.setText("登录");

        mEditName.setText(SPUtils.getSP().getString(Constant.PRE_KEY_LOGIN_NAME,""));
        mEditPasswd.setText(SPUtils.getSP().getString(Constant.PRE_KEY_LOGIN_PASSWD,""));

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
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

                ThreadUtils.getCachedPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        HttpPost httpPost = HttpPost.getInstance();
                        if(httpPost.login(user)){
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Utils.showToast("登录成功");
                                    SPUtils.save(Constant.PRE_KEY_LOGIN_NAME, user.getName());
                                    SPUtils.save(Constant.PRE_KEY_LOGIN_PASSWD, user.getPasswd());
                                    SPUtils.save(Constant.PRE_KEY_USER_ID, user.getUserid());
                                    SPUtils.save(Constant.PRE_KEY_LOGIN_STATUS, true);
                                    NetworkDataManager.getsInstance().sync();
                                    finish();
                                }
                            });
                        }else{
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    SPUtils.save(Constant.PRE_KEY_LOGIN_STATUS, false);
                                    Utils.showToast("登录失败，请检查网络");
                                }
                            });
                        }
                    }
                });
            }
        });

    }
}
