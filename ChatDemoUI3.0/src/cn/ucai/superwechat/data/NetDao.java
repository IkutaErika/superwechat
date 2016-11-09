package cn.ucai.superwechat.data;

import android.content.Context;
import android.util.Log;

import com.hyphenate.easeui.domain.User;

import java.io.File;

import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.ui.AddContactActivity;
import cn.ucai.superwechat.ui.FriendProfileActivity;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.MD5;
import cn.ucai.superwechat.widget.I;


/**
 * Created by Administrator on 2016/10/18.
 */
public class NetDao {
    Context mcontext;
    int mgoodsid;

    public NetDao(Context mcontext, int mgoodsid) {
        this.mcontext = mcontext;
        this.mgoodsid = mgoodsid;
    }
    public NetDao(Context mcontext) {
        this.mcontext = mcontext;
    }

    public static void register(Context mcontext, String username, String nickname, String password, OkHttpUtils.OnCompleteListener<Result> onCompleteListener) {
        OkHttpUtils<Result> utils=new OkHttpUtils<>(mcontext);
        utils.url(I.SERVER_ROOT+I.REQUEST_REGISTER)
                .addParam(I.User.USER_NAME,username)
                .addParam(I.User.NICK,nickname)
                .addParam(I.User.PASSWORD, MD5.getMessageDigest(password))
                .targetClass(Result.class)
                .post()
                .execute(onCompleteListener);
    }
    public static void unregister(Context mcontext, String username, OkHttpUtils.OnCompleteListener<Result> onCompleteListener) {
        OkHttpUtils<Result> utils=new OkHttpUtils<>(mcontext);
        utils.url(I.SERVER_ROOT+I.REQUEST_UNREGISTER)
                .addParam(I.User.USER_NAME,username)
                .targetClass(Result.class)
                .execute(onCompleteListener);

    }



    public static void loginin(Context mcontext, String username, String password, OkHttpUtils.OnCompleteListener<String> onCompleteListener) {
        OkHttpUtils<String> utils=new OkHttpUtils<>(mcontext);
        utils.url(I.SERVER_ROOT+I.REQUEST_LOGIN)
                .addParam(I.User.USER_NAME,username)
                .addParam(I.User.PASSWORD, MD5.getMessageDigest(password))
                .targetClass(String.class)
                .execute(onCompleteListener);
    }

    public static void updateNickname(Context context,String muserName, String muserNick,OkHttpUtils.OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils=new OkHttpUtils<>(context);
        utils.url(I.SERVER_ROOT+I.REQUEST_UPDATE_USER_NICK)
                .addParam(I.User.USER_NAME,muserName)
                .addParam(I.User.NICK,muserNick)
                .targetClass(String.class)
                .execute(listener);

    }

    public static void updateAvatar(Context context, String muserName, File file, OkHttpUtils.OnCompleteListener<String> onCompleteListener) {
        OkHttpUtils<String> utils=new OkHttpUtils<>(context);
        utils.url(I.SERVER_ROOT+I.REQUEST_UPDATE_AVATAR)
             .addParam(I.NAME_OR_HXID,muserName)
             .addParam(I.AVATAR_TYPE,"user_avatar")
             .addFile2(file)
             .post()
            .targetClass(String.class)
            .execute(onCompleteListener);

   }

    public static void searchUser(Context mContext, String toAddUsername, OkHttpUtils.OnCompleteListener<String> onCompleteListener) {
        OkHttpUtils<String> utils=new OkHttpUtils<>(mContext);
        utils.url(I.SERVER_ROOT+I.REQUEST_FIND_USER)
                .addParam(I.User.USER_NAME,toAddUsername)
                .targetClass(String.class)
                .execute(onCompleteListener);
    }
    public static void addContact(Context mcontext, String username, String othername, OkHttpUtils.OnCompleteListener<String> onCompleteListener) {
        OkHttpUtils<String> utils=new OkHttpUtils<>(mcontext);
        utils.url(I.SERVER_ROOT+I.REQUEST_ADD_CONTACT)
                .addParam(I.Contact.USER_NAME,username)
                .addParam(I.Contact.CU_NAME, othername)
                .targetClass(String.class)
                .execute(onCompleteListener);
    }

    public static void deleteContact(Context appContext, String currentuser, String username, OkHttpUtils.OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils=new OkHttpUtils<>(appContext);
        utils.url(I.SERVER_ROOT+I.REQUEST_DELETE_CONTACT)
                .addParam(I.Contact.USER_NAME,currentuser)
                .addParam(I.Contact.CU_NAME, username)
                .targetClass(String.class)
                .execute(listener);
    }

    public static void downloadAllFriends(Context friendProfileActivity, String currentuser, OkHttpUtils.OnCompleteListener<String> onCompleteListener) {
        OkHttpUtils<String> utils=new OkHttpUtils<>(friendProfileActivity);
        utils.url(I.SERVER_ROOT+I.REQUEST_DOWNLOAD_CONTACT_ALL_LIST)
                .addParam(I.Contact.USER_NAME,currentuser)
                .targetClass(String.class)
                .execute(onCompleteListener);
    }
}
