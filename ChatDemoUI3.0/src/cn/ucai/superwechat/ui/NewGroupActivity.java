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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMGroupManager.EMGroupOptions;
import com.hyphenate.chat.EMGroupManager.EMGroupStyle;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.domain.Group;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseImageUtils;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.exceptions.HyphenateException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.NetDao;
import cn.ucai.superwechat.data.OkHttpUtils;
import cn.ucai.superwechat.utils.CommonUtils;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.ResultUtils;
import cn.ucai.superwechat.widget.I;
import cn.ucai.superwechat.widget.SuperwechatHelper;

public class NewGroupActivity extends BaseActivity {
    private static final int REQUESTCODE_PICK = 1;
    private static final int
            REQUESTCODE_CUTTING = 2;
    private static final int
            REQUESTCODE_MEMBER = 3;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.btn_save)
    Button btnSave;
    @Bind(R.id.edit_group_name)
    EditText editGroupName;
    @Bind(R.id.edit_group_introduction)
    EditText editGroupIntroduction;
    @Bind(R.id.iv_groupavatar)
    ImageView ivGroupavatar;
    @Bind(R.id.cb_public)
    CheckBox cbPublic;
    @Bind(R.id.cb_member_inviter)
    CheckBox cbMemberInviter;
    @Bind(R.id.second_desc)
    TextView secondDesc;
    private ProgressDialog progressDialog;
    EMGroup emGroup;
    File avatarFile=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_new_group);
        ButterKnife.bind(this);
       setListener();

    }

    private void setListener() {
        cbPublic.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    secondDesc.setText(R.string.join_need_owner_approval);
                } else {
                    secondDesc.setText(R.string.Open_group_members_invited);
                }
            }
        });
    }


    public void save() {
        String name = editGroupName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            new EaseAlertDialog(this, R.string.Group_name_cannot_be_empty).show();
        } else {
            // select from contact list
            startActivityForResult(new Intent(this, GroupPickContactsActivity.class).putExtra("groupName", name), REQUESTCODE_MEMBER);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUESTCODE_PICK:
                if (data == null || data.getData() == null) {
                    return;
                }
                startPhotoZoom(data.getData());
                break;
            case REQUESTCODE_CUTTING:
                if (data != null) {
                 setPicToView(data);
                }
                break;
            case REQUESTCODE_MEMBER:
                if (resultCode == RESULT_OK) {
                    CreateEmGroup(data);
                }
                break;
            default:

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);


    }

    private void CreateEmGroup(final Intent data) {
        String st1 = getResources().getString(R.string.Is_to_create_a_group_chat);
        final String st2 = getResources().getString(R.string.Failed_to_create_groups);
        //new group
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(st1);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String groupName = editGroupName.getText().toString().trim();
                String desc = editGroupIntroduction.getText().toString();
                String[] members = data.getStringArrayExtra("newmembers");
                try {
                    EMGroupOptions option = new EMGroupOptions();
                    option.maxUsers = 200;
                    String reason = NewGroupActivity.this.getString(R.string.invite_join_group);
                    reason = EMClient.getInstance().getCurrentUser() + reason + groupName;
                    if (cbPublic.isChecked()) {
                        option.style = cbMemberInviter.isChecked() ? EMGroupStyle.EMGroupStylePublicJoinNeedApproval : EMGroupStyle.EMGroupStylePublicOpenJoin;
                    } else {
                        option.style = cbMemberInviter.isChecked() ? EMGroupStyle.EMGroupStylePrivateMemberCanInvite : EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
                    }
                   emGroup= EMClient.getInstance().groupManager().createGroup(groupName, desc, members, reason, option);
                    CreateAppgroup(emGroup.getGroupId(), emGroup.getGroupName(), emGroup.getDescription(), emGroup.getOwner(), emGroup.isPublic(), emGroup.isAllowInvites(),avatarFile);
                } catch (final HyphenateException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(NewGroupActivity.this, st2 + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        }).start();
    }

    private void createsuccess() {
        runOnUiThread(new Runnable() {
            public void run() {
                progressDialog.dismiss();
                setResult(RESULT_OK);
                finish();
            }
        });
    }
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }
    private void CreateAppgroup(String groupId, String groupName, String description, String owner, boolean aPublic, boolean allowInvites,File avatarFile) {
        if (avatarFile==null)
        {
            NetDao.createNewGroup(this, groupId, groupName, description, owner, aPublic, allowInvites, new OkHttpUtils.OnCompleteListener<String>() {
                @Override
                public void onSuccess(String result) {
                    afterCreateGroup(result);
                }

                @Override
                public void onError(String error) {

                }
            });
        }
        else {
            NetDao.createNewGroup(this, groupId, groupName, description, owner, aPublic, allowInvites, avatarFile, new OkHttpUtils.OnCompleteListener<String>() {
                @Override
                public void onSuccess(String result) {
                   afterCreateGroup(result);
                }



                @Override
                public void onError(String error) {

                }
            });
        }


    }

    private void afterCreateGroup(String result) {
          if (result!=null)
          {
              Result result1=ResultUtils.getResultFromJson(result, Group.class);
              if (result1!=null&&result1.isRetMsg())
              {
                  Group group = (Group) result1.getRetData();
                  createsuccess();

              }
          }
    }

    @OnClick({R.id.iv_back, R.id.btn_save, R.id.group_avatar, R.id.cb_public, R.id.cb_member_inviter})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                this.finish();
                break;
            case R.id.btn_save:
              save();
                break;
            case R.id.group_avatar:
                uploadHeadPhoto();
                break;
            case R.id.cb_public:
                break;
            case R.id.cb_member_inviter:
                break;
        }
    }
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(getResources(), photo);
            ivGroupavatar.setImageDrawable(drawable);
            saveBitmapFile(picdata);
            //    uploadUserAvatar(Bitmap2Bytes(photo));
        }

    }
    public  void saveBitmapFile(Intent picData){
        Bundle extra=picData.getExtras();
        if (extra!=null) {
            Bitmap bitmap = extra.getParcelable("data");
            String imagepath = EaseImageUtils.getImagePath(System.currentTimeMillis() + I.AVATAR_SUFFIX_PNG);
            File file = new File(imagepath);//要保存的图片路径
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                bos.flush();
                bos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            avatarFile = file;

        }
    }
    private void uploadHeadPhoto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dl_title_upload_photo);
        builder.setItems(new String[]{getString(R.string.dl_msg_take_photo), getString(R.string.dl_msg_local_upload)},
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                Toast.makeText(NewGroupActivity.this, getString(R.string.toast_no_support),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                startActivityForResult(pickIntent, REQUESTCODE_PICK);
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.create().show();
    }
}
