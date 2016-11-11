package cn.ucai.superwechat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseUserUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.NetDao;
import cn.ucai.superwechat.data.OkHttpUtils;
import cn.ucai.superwechat.db.UserDao;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.utils.ResultUtils;
import cn.ucai.superwechat.widget.SuperwechatHelper;

/**
 * Created by Administrator on 2016/11/7.
 */
public class FriendProfileActivity extends BaseActivity {
    User user = null;
    String username=null;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.iv_profile_avatar)
    ImageView ivProfileAvatar;
    @Bind(R.id.tv_profile_nickname)
    TextView tvProfileNickname;
    @Bind(R.id.tv_profile_username)
    TextView tvProfileUsername;
    @Bind(R.id.btn_sendmessage)
    Button btnSendmessage;
    @Bind(R.id.btn_sendvideo)
    Button btnSendvideo;
    @Bind(R.id.btn_Add_contacts)
    Button btnAddContacts;
    boolean isFriends;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        ButterKnife.bind(this);
        initview();
    }

    private void initview() {
        username=getIntent().getStringExtra("user");
        if(username==null)
        {
            MFGT.finish(this);
            return;
        }
        user = SuperwechatHelper.getInstance().getAppContactList().get(getIntent().getStringExtra("user"));
        if (user == null) {
            isFriends=false;
        }
        else {
            setUserInfo();
            isFriends=true;
        }
        isFriend(isFriends);
        syncUserInfo();

}
private  void  synfails(){
    MFGT.finish(this);
    return;
}
    private void syncUserInfo() {
           NetDao.searchUser(this,username, new OkHttpUtils.OnCompleteListener<String>() {
               @Override
               public void onSuccess(String s) {
                    if (s!=null)
                    {
                        Result result = ResultUtils.getResultFromJson(s,User.class);
                        if (result!=null&&result.isRetMsg())
                        {
                          User  u= (User) result.getRetData();
                            if (u!=null)
                            {
                                if (isFriends)
                                {
                                    SuperwechatHelper.getInstance().saveAppContact(u);
                                }
                                else {
                                    SuperwechatHelper.getInstance().savetoNoFriends(u);
                                }
                                user=u;
                                setUserInfo();
                            }
                            else {
                                synfails();
                            }
                        }
                        else {
                            synfails();
                        }
                    }
                    else {
                        synfails();
                    }
               }

               @Override
               public void onError(String error) {

               }
           });
    }

    private void isFriend(boolean isFriends) {
        if (isFriends)
        {
                btnSendmessage.setVisibility(View.VISIBLE);
                btnSendvideo.setVisibility(View.VISIBLE);
        }
        else {
            btnAddContacts.setVisibility(View.VISIBLE);
        }

     }

    private void setUserInfo() {
        EaseUserUtils.setCurrentAppUserAvatar(this, user, ivProfileAvatar);
        EaseUserUtils.setCurrentAppUserNick(user.getMUserNick(), tvProfileNickname);
        EaseUserUtils.setCurrentAppUserNameWithNo(user.getMUserName(), tvProfileUsername);
    }


    @OnClick({R.id.iv_back, R.id.btn_Add_contacts, R.id.btn_sendmessage, R.id.btn_sendvideo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                this.finish();
                break;
            case R.id.btn_Add_contacts:
                MFGT.gotoAddFrientProfile(FriendProfileActivity.this,user);
                break;
            case R.id.btn_sendmessage:
                MFGT.gotoChat(this,user);
                break;
            case R.id.btn_sendvideo:
                MFGT.gotoVideo(FriendProfileActivity.this,user.getMUserName(),false);

                break;
        }
    }
}
