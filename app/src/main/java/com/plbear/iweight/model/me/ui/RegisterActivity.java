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
import com.plbear.iweight.utils.ThreadUtils;
import com.plbear.iweight.utils.Utils;

import butterknife.BindView;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity {
    @BindView(R.id.edit_name) EditText mEditName;
    @BindView(R.id.btn_back) View mViewBack;
    @BindView(R.id.lab_title) TextView mLabTitle;
    @BindView(R.id.lab_notify) TextView mLabNotify;
    @BindView(R.id.edit_passwd) EditText mEditPasswd;
    @BindView(R.id.edit_passwd_double) EditText mEditPasswdDouble;
    @BindView(R.id.btn_register) View mBtnRegister;


    @Override
    public int getLayout() {
        return R.layout.activity_register;
    }

    @Override
    public void afterLayout() {
        mViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mLabTitle.setText("注册");
        mLabNotify.setVisibility(View.INVISIBLE);
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mEditName.getText().toString();
                String passwd = mEditPasswd.getText().toString();
                String passwdDouble = mEditPasswdDouble.getText().toString();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(passwd) || TextUtils.isEmpty(passwdDouble)) {
                    mLabNotify.setVisibility(View.VISIBLE);
                    mLabNotify.setText("请输入用户名和密码");
                    return;
                }

                if (!passwd.equals(passwdDouble)) {
                    mLabNotify.setVisibility(View.VISIBLE);
                    mLabNotify.setText("两次密码不一致");
                    return;
                }

                final User user = new User();
                user.setName(name);
                user.setPasswd(passwd);

                ThreadUtils.getCachedPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        HttpPost httpPost = HttpPost.getInstance();
                        if (httpPost.create(user)) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Utils.showToast("注册成功");
                                    SPUtils.save(Constant.PRE_KEY_LOGIN_NAME, user.getName());
                                    SPUtils.save(Constant.PRE_KEY_LOGIN_PASSWD, user.getPasswd());
                                    SPUtils.save(Constant.PRE_KEY_USER_ID, user.getUserid());
                                    finish();
                                }
                            });
                        } else {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    SPUtils.save(Constant.PRE_KEY_LOGIN_STATUS, false);
                                    Utils.showToast("注册失败，请检查网络");
                                }
                            });
                        }
                    }
                });
            }
        });


    }
}
