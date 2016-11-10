package cn.ucai.superwechat.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.redpacketui.utils.RedPacketUtil;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseUserUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.widget.SuperwechatHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFrament extends Fragment {


    @Bind(R.id.iv_profile_avatar)
    ImageView ivProfileAvatar;
    @Bind(R.id.tv_profile_nickname)
    TextView tvProfileNickname;
    @Bind(R.id.tv_profile_username)
    TextView tvProfileUsername;

    public ProfileFrament() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_frament, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        setUserInfo();
    }

    private void setUserInfo() {
        User user =SuperwechatHelper.getInstance().getCurrentuser();
        EaseUserUtils.setCurrentAppUserAvatar(getActivity(),user,ivProfileAvatar);
        EaseUserUtils.setCurrentAppUserNick(user,tvProfileNickname);
        EaseUserUtils.setCurrentAppUserNameWithNo(tvProfileUsername);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.layout_profile_view, R.id.tv_profile_money, R.id.tv_profile_settings})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_profile_view:
                MFGT.startActivity(getActivity(),UserProfileActivity.class);
                break;
            case R.id.tv_profile_money:
                //red packet code : 进入零钱页面
                RedPacketUtil.startChangeActivity(getActivity());
            //end of red packet code
                break;

            case R.id.tv_profile_settings:
                MFGT.startActivity(getActivity(),SettingsActivity.class);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setUserInfo();
    }
}
