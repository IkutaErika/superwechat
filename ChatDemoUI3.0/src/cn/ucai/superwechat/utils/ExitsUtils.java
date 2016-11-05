package cn.ucai.superwechat.utils;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/5.
 */
public class ExitsUtils {
    List<Activity> activityList=new LinkedList<>();
private  static ExitsUtils instance=new ExitsUtils();

    public ExitsUtils() {
    }

    public static ExitsUtils getInstance() {
        return instance;
    }
    public void addActivity(Activity activity){
        activityList.add(activity);
    }
    public void removeActivity(Activity activity){
        activityList.remove(activity);
    }
    public  void  Exits(){
        for (Activity activity:activityList)
        {
            activity.finish();
        }
    }
}
