package cn.ucai.superwechat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.io.Serializable;

import cn.ucai.superwechat.R;
/**
 * Created by Administrator on 2016/10/22.
 */
public class BundleUtils {
static     Bundle bundle=new Bundle();
    public static void putInt(String id, int i) {

        bundle.putInt(id,i);
    }

    public static void putSerializable(String user, Object userBean) {
        bundle.putSerializable(user, (Serializable) userBean);
    }

    public static void intent(Activity activity, Class<?> clas) {
        Intent intent = new Intent();
        intent.setClass(activity,clas);
        intent.putExtras(bundle);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
    }

    public static void putString(String key, String value) {
        bundle.putString(key,value);
    }
}
