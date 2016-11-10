package cn.ucai.superwechat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.hyphenate.easeui.domain.User;

import cn.ucai.superwechat.R;
import cn.ucai.superwechat.ui.AddContactActivity;
import cn.ucai.superwechat.ui.AddFriendActivity;
import cn.ucai.superwechat.ui.ChatActivity;
import cn.ucai.superwechat.ui.FriendProfileActivity;
import cn.ucai.superwechat.ui.MainActivity;
import cn.ucai.superwechat.ui.UserProfileActivity;
import cn.ucai.superwechat.ui.VideoCallActivity;


public class MFGT {
    public static void finish(Activity activity){
        activity.finish();
        activity.overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
    }
    public static void gotoMainActivity(Activity context){
        startActivity(context, MainActivity.class);
    }
    public static void startActivity(Activity context,Class<?> cls){
        Intent intent = new Intent();
        intent.setClass(context,cls);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
    }
    public static void startActivity(Activity context,Intent intent){
        ((Activity)context).startActivity(intent);
        context.overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
    }

  public static void gotoFrientProfile(Activity activity, User user) {
        Intent intent=new Intent();
        Bundle bundle=new Bundle();
        bundle.putSerializable("currentuser",user);
         bundle.putInt("style",2);
        intent.putExtras(bundle);
        intent.setClass(activity,FriendProfileActivity.class);
        activity.startActivity(intent);
    }
    public static void gotoFrientProfile(Activity activity, String username) {
        Intent intent=new Intent();
        intent.putExtra("user",username);
        intent.setClass(activity,FriendProfileActivity.class);
        activity.startActivity(intent);
    }
    public static void gotoAddFrientProfile(Activity activity, User user) {
        Intent intent=new Intent();
        Bundle bundle=new Bundle();
        bundle.putSerializable("user",user);
        intent.putExtras(bundle);
        intent.setClass(activity,AddFriendActivity.class);
        activity.startActivity(intent);
    }
    public static void gotoChat(Activity activity, User user) {
        Intent intent=new Intent();
        intent.putExtra("userId",user.getMUserName());
        intent.setClass(activity,ChatActivity.class);
        activity.startActivity(intent);
    }

    public static void gotoUsersProfile(Activity activity, String username) {
        Intent intent=new Intent();
        intent.putExtra("user",username);
        intent.setClass(activity,UserProfileActivity.class);
        activity.startActivity(intent);
    }

    public static void gotoVideo(Activity activity, String mUserName, boolean b) {
        Intent intent=new Intent();
        intent.putExtra("username",mUserName);
        intent.putExtra("isComingCall",b);
        intent.setClass(activity,VideoCallActivity.class);
        activity.startActivity(intent);
    }
}
