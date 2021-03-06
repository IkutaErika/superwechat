/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ucai.superwechat.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.NetDao;
import cn.ucai.superwechat.data.OkHttpUtils;
import cn.ucai.superwechat.utils.CommonUtils;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.MD5;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.widget.I;
import cn.ucai.superwechat.widget.SuperwechatHelper;
import cn.ucai.superwechat.R;

/**
 * register screen
 *
 */
public class RegisterActivity extends BaseActivity {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.ed_username)
    EditText edUsername;
    @Bind(R.id.ed_nickname)
    EditText edNickname;
    @Bind(R.id.ed_password)
    EditText edPassword;
    @Bind(R.id.ed_confirm_password)
    EditText edConfirmPassword;
    @Bind(R.id.btn_register)
    Button btnRegister;
   String nickname;
   String username;
    String pwd ;
    String confirm_pwd;
     ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_register);
        ButterKnife.bind(this);
    }

    public void register() {
         username = edUsername.getText().toString().trim();
         pwd = edPassword.getText().toString().trim();
       confirm_pwd = edConfirmPassword.getText().toString().trim();
         nickname = edNickname.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "用户名不能为空！", Toast.LENGTH_SHORT).show();
            edUsername.requestFocus();
            return;
        } else if (!username.matches("[A-Za-z]\\w{5,15}")) {
            Toast.makeText(this, "用户名非法！", Toast.LENGTH_SHORT).show();
            edUsername.requestFocus();
            return;
        } else if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, "密码不能为空！", Toast.LENGTH_SHORT).show();
            edPassword.requestFocus();
            return;
        } else if (TextUtils.isEmpty(confirm_pwd)) {
            Toast.makeText(this, "确认密码不能为空！", Toast.LENGTH_SHORT).show();
            edConfirmPassword.requestFocus();
            return;
        } else if (TextUtils.isEmpty(nickname)) {
            Toast.makeText(this, "昵称不能为空！", Toast.LENGTH_SHORT).show();
            edNickname.requestFocus();
            return;
        } else if (!pwd.equals(confirm_pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Two_input_password), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(nickname)) {
            pd = new ProgressDialog(this);
            pd.setMessage(getResources().getString(R.string.Is_the_registered));
            pd.show();

           registerAppService();

        }
    }

    private void registerAppService() {
        NetDao.register(RegisterActivity.this, username, nickname, pwd, new OkHttpUtils.OnCompleteListener<Result>() {
            @Override
            public void onSuccess(Result result) {
                if (result==null)
                {
                    pd.dismiss();
                }
                else {
                    if (result!=null&&result.isRetMsg())
                    {
                        registerEMService();
                    }
                    else {
                        if (result.getRetCode()== I.MSG_REGISTER_USERNAME_EXISTS){
                            CommonUtils.showmsgShortToast(result.getRetCode());
                            pd.dismiss();
                        }
                        else {
                            unregisterAppService();
                        }

                    }
                }

            }

            @Override
            public void onError(String error) {
                    pd.dismiss();
            }
        });


    }

    private void unregisterAppService() {
        NetDao.unregister(RegisterActivity.this, username, new OkHttpUtils.OnCompleteListener<Result>() {
            @Override
            public void onSuccess(Result result) {
             pd.dismiss();
            }

            @Override
            public void onError(String error) {
                pd.dismiss();
            }
        });
    }

    private void registerEMService() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    // call method in SDK
                    EMClient.getInstance().createAccount(username, MD5.getMessageDigest(pwd));
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (!RegisterActivity.this.isFinishing())
                                pd.dismiss();
                            // save current user
                            SuperwechatHelper.getInstance().setCurrentUserName(username);
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registered_successfully), Toast.LENGTH_SHORT).show();
                            MFGT.finish(RegisterActivity.this);
                        }
                    });
                } catch (final HyphenateException e) {
                    unregisterAppService();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (!RegisterActivity.this.isFinishing())
                                pd.dismiss();
                            int errorCode = e.getErrorCode();
                            if (errorCode == EMError.NETWORK_ERROR) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.USER_ALREADY_EXIST) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.USER_AUTHENTICATION_FAILED) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.USER_ILLEGAL_ARGUMENT) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registration_failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }

    public void back(View view) {
        finish();
    }

    @OnClick({R.id.iv_back, R.id.btn_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                MFGT.finish(this);
                break;
            case R.id.btn_register:
                register();
                break;
        }
    }
}
