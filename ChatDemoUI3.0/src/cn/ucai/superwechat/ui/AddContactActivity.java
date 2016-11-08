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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseAlertDialog;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.NetDao;
import cn.ucai.superwechat.data.OkHttpUtils;
import cn.ucai.superwechat.utils.CommonUtils;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.utils.ResultUtils;
import cn.ucai.superwechat.widget.SuperwechatHelper;

public class AddContactActivity extends BaseActivity {
    @Bind(R.id.edit_username)
    EditText editUsername;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.search)
    Button search;
    private String toAddUsername;
    private RelativeLayout searchedUserLayout;
    private Button searchBtn;
     private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_add_contact);
        ButterKnife.bind(this);
    }

   public void searchContact() {
        final String name = editUsername.getText().toString();
           toAddUsername = name;
           if (TextUtils.isEmpty(name)) {
               new EaseAlertDialog(this, R.string.Please_enter_a_username).show();
               return;
           }
             progressDialog=new ProgressDialog(this);
          String str1=getResources().getString(R.string.addcontact_search);
       progressDialog.setMessage(str1);
       progressDialog.setCanceledOnTouchOutside(false);
       progressDialog.show();
           // TODO you can search the user from your app server here.
         searchAppUser();

       }

    private void searchAppUser() {
        NetDao.searchUser(this,toAddUsername, new OkHttpUtils.OnCompleteListener<String>() {
            @Override
            public void onSuccess(String result) {
         if (result!=null)
         {
             Result res= ResultUtils.getResultFromJson(result, User.class);
             if (res!=null&&res.isRetMsg())
             {
                 User user= (User) res.getRetData();
                 EaseUser euser=new EaseUser(user.getMUserName());
                 euser.setNickname(user.getMUserNick());
                 euser.setAvatar(euser.getAvatar());
                 SuperwechatHelper.getInstance().saveContact(euser);
                 if (user!=null&&res.isRetMsg())
                 {
                     MFGT.gotoFrientProfile(AddContactActivity.this,user);
                 }
                 progressDialog.dismiss();
             }
             else {
                 CommonUtils.showShortToast(R.string.failed_to_search);
                 progressDialog.dismiss();
             }
         }else {
             CommonUtils.showShortToast(R.string.failed_to_search);

             progressDialog.dismiss();
         }

            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
            }
        });
    }

    /*
      public void addContact(View view) {
          if (EMClient.getInstance().getCurrentUser().equals(nameText.getText().toString())) {
              new EaseAlertDialog(this, R.string.not_add_myself).show();
              return;
          }

        *//*  if (SuperwechatHelper.getInstance().getContactList().containsKey(nameText.getText().toString())) {
            //let the user know the contact already in your contact list
            if (EMClient.getInstance().contactManager().getBlackListUsernames().contains(nameText.getText().toString())) {
                new EaseAlertDialog(this, R.string.user_already_in_contactlist).show();
                return;
            }
            new EaseAlertDialog(this, R.string.This_user_is_already_your_friend).show();
            return;
        }
*//*
        progressDialog = new ProgressDialog(this);
        String stri = getResources().getString(R.string.Is_sending_a_request);
        progressDialog.setMessage(stri);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        new Thread(new Runnable() {
            public void run() {

                try {
                    //demo use a hardcode reason here, you need let user to input if you like
                    String s = getResources().getString(R.string.Add_a_friend);
                //    EMClient.getInstance().contactManager().addContact(toAddUsername, s);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s1 = getResources().getString(R.string.send_successful);
                            Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s2 = getResources().getString(R.string.Request_add_buddy_failure);
                            Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }
*/

    @OnClick({R.id.iv_back, R.id.search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                this.finish();
                break;
            case R.id.search:
                searchContact();
                break;
        }
    }
}
