package cn.ucai.superwechat.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseUserUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.utils.MFGT;

/**
 * Created by Administrator on 2016/11/7.
 */
public class FriendProfileActivity extends BaseActivity {
    User user = null;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.iv_user_profile)
    ImageView ivUserProfile;
    @Bind(R.id.tv_nickname_profile)
    TextView tvNicknameProfile;
    @Bind(R.id.tv_username_profile)
    TextView tvUsernameProfile;

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
    }

    private void setUserInfo() {
        EaseUserUtils.setCurrentAppUserAvatar(this,user.getMUserName(),ivUserProfile);
        EaseUserUtils.setCurrentAppUserNick(user.getMUserNick(),tvNicknameProfile);
        EaseUserUtils.setCurrentAppUserNameWithNo(user.getMUserName(),tvUsernameProfile);
    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        this.finish();
    }
}
