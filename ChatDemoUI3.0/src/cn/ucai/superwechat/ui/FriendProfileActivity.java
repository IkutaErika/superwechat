package cn.ucai.superwechat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseUserUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.widget.SuperwechatHelper;

/**
 * Created by Administrator on 2016/11/7.
 */
public class FriendProfileActivity extends BaseActivity {
    User user = null;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        ButterKnife.bind(this);
        user = (User) getIntent().getSerializableExtra("user");
        if (user == null) {
            MFGT.finish(this);
        }
        setUserInfo();
        isFriend();
    }

    private void isFriend() {
        if (SuperwechatHelper.getInstance().getAppContactList().containsKey(user.getMUserName())) {
            btnSendmessage.setVisibility(View.VISIBLE);
            btnSendvideo.setVisibility(View.VISIBLE);
        } else {
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
                break;
        }
    }
}
