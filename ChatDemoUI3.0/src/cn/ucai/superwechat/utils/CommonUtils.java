package cn.ucai.superwechat.utils;

import android.widget.Toast;

import cn.ucai.superwechat.R;
import cn.ucai.superwechat.widget.I;
import cn.ucai.superwechat.widget.SuperwechatApplication;

public class CommonUtils {
    public static void showLongToast(String msg){
        Toast.makeText(SuperwechatApplication.getInstance(),msg,Toast.LENGTH_LONG).show();
    }
    public static void showShortToast(String msg){
        Toast.makeText(SuperwechatApplication.getInstance(),msg,Toast.LENGTH_SHORT).show();
    }
    public static void showLongToast(int rId){
        showLongToast(SuperwechatApplication.getInstance().getString(rId));
    }
    public static void showShortToast(int rId){
        showShortToast(SuperwechatApplication.getInstance().getString(rId));
    }
    public static void showmsgShortToast(int msgId){
        if (msgId>0){
            showShortToast(SuperwechatApplication.getInstance().getResources().getIdentifier(I.MSG_PREFIX_MSG+msgId,"string",SuperwechatApplication.getInstance().getPackageName()));
        }
       else {
            showShortToast(R.string.no_msg_1);

        }
    }
}
