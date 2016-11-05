package com.hyphenate.easeui.utils;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.controller.EaseUI.EaseUserProfileProvider;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.domain.User;

public class EaseUserUtils {
    
    static EaseUserProfileProvider userProvider;
    
    static {
        userProvider = EaseUI.getInstance().getUserProfileProvider();
    }
    
    /**
     * get EaseUser according username
     * @param username
     * @return
     */
    public static EaseUser getUserInfo(String username){
        if(userProvider != null)
            return userProvider.getUser(username);
        
        return null;
    }
    public static User getAppUserInfo(){
        if(userProvider != null)
            return userProvider.getAppUser();

        return null;
    }


    /**
     * set user avatar
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView){
    	EaseUser user = getUserInfo(username);
        if(user != null && user.getAvatar() != null){
            try {
                int avatarResId = Integer.parseInt(user.getAvatar());
                Glide.with(context).load(avatarResId).into(imageView);
            } catch (Exception e) {
                //use default avatar
                Glide.with(context).load(user.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ease_default_avatar).into(imageView);
            }
        }else{
            Glide.with(context).load(R.drawable.ease_default_avatar).into(imageView);
        }
    }
    
    /**
     * set user's nickname
     */
    public static void setUserNick(String userNick,TextView textView){
        if(textView != null){
        	EaseUser user = getUserInfo(userNick);
        	if(user != null && user.getNick() != null){
        		textView.setText(user.getNick());
        	}else{
        		textView.setText(userNick);
        	}
        }
    }
    /**
     * set user avatar
     * @param username
     */
    public static void setAppUserAvatar(Context context, String username, ImageView imageView){
        User user = getAppUserInfo();
        if(user != null && user.getAvatar() != null){
            try {
                int avatarResId = Integer.parseInt(user.getAvatar());
                Glide.with(context).load(avatarResId).into(imageView);
            } catch (Exception e) {
                //use default avatar
                Glide.with(context).load(user.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_hd_avatar).into(imageView);
            }
        }else{
            Glide.with(context).load(R.drawable.default_hd_avatar).into(imageView);
        }
    }

    /**
     * set user's nickname
     */
    public static void setAppUserNick(String userNick,TextView textView){
        if(textView != null){
            User user = getAppUserInfo();
            if(user != null && user.getMUserNick() != null){
                textView.setText(user.getMUserNick());
            }else{
                textView.setText(userNick);
            }
        }
    }

    public static void setCurrentAppUserAvatar(FragmentActivity activity, ImageView iv) {
        String username= EMClient.getInstance().getCurrentUser();
        setAppUserAvatar(activity,username,iv);
    }

    public static void setCurrentAppUserNick(TextView tvProfileNickname) {
        String username=EMClient.getInstance().getCurrentUser();
        setAppUserNick(username,tvProfileNickname);
    }

    public static void setCurrentAppUserNameWithNo(TextView tvProfileUsername) {
        String username=EMClient.getInstance().getCurrentUser();
        setAppUserName(username,tvProfileUsername);
    }

    private static void setAppUserName(String username, TextView tvProfileUsername) {
        tvProfileUsername.setText("微信号："+username);

    }

    public static void setCurrentAppUserName(TextView tvUsernameProfile) {
        String username=EMClient.getInstance().getCurrentUser();
        tvUsernameProfile.setText(username);
    }

    public static User getCurrentAppUserInfo() {
        String username=EMClient.getInstance().getCurrentUser();
       if (userProvider!=null)
       return userProvider.getAppUser();
        return null;
    }
}
