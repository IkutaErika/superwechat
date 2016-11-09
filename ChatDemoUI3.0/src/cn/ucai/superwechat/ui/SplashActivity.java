package cn.ucai.superwechat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseUserUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.NetDao;
import cn.ucai.superwechat.data.OkHttpUtils;
import cn.ucai.superwechat.db.UserDao;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.ResultUtils;
import cn.ucai.superwechat.widget.SuperwechatHelper;
import cn.ucai.superwechat.R;

/**
 * 开屏页
 */
public class SplashActivity extends BaseActivity {

    private static final int sleepTime = 2000;
    @Bind(R.id.splash_root)
    RelativeLayout rootLayout;

    @Override
    protected void onCreate(Bundle arg0) {
        setContentView(R.layout.em_activity_splash);
        ButterKnife.bind(this);
        super.onCreate(arg0);
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(1500);
        rootLayout.startAnimation(animation);
    }

    @Override
    protected void onStart() {
        super.onStart();

        new Thread(new Runnable() {
            public void run() {
                if (SuperwechatHelper.getInstance().isLoggedIn()) {
                    // auto login mode, make sure all group and conversation is loaed before enter the main screen
                    long start = System.currentTimeMillis();
                    EMClient.getInstance().groupManager().loadAllGroups();
                    EMClient.getInstance().chatManager().loadAllConversations();
                    UserDao dao =new UserDao(SplashActivity.this);
                    final User user= dao.getUsers(EMClient.getInstance().getCurrentUser());
                    if (user!=null)
                    {
                        NetDao.downloadAllFriends(SplashActivity.this,SuperwechatHelper.getInstance().getCurrentuser().getMUserName(), new OkHttpUtils.OnCompleteListener<String>() {
                            @Override
                            public void onSuccess(String result) {
                                L.e("SpLASH"+result);
                                if (result==null) {
                               return;
                                }
                                else {
                                    Result re = ResultUtils.getListResultFromJson(result, User.class);
                                    if (re.isRetMsg()&&re.getRetData() != null) {
                                        ArrayList<User> userlist = (ArrayList<User>) re.getRetData();
                                        SuperwechatHelper.getInstance().getAppContactList().clear();
                                        SuperwechatHelper.getInstance().getContactList().clear();
                                        for (int i=0;i<userlist.size();i++) {
                                            SuperwechatHelper.getInstance().saveAppContact(userlist.get(i));
                                            EaseUser user1 =new EaseUser(userlist.get(i).getMUserName());
                                            user1.setAvatar(userlist.get(i).getAvatar());
                                            user1.setNickname(userlist.get(i).getMUserNick());
                                            SuperwechatHelper.getInstance().saveContact(user1);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onError(String error) {

                            }
                        });

                    }
                    SuperwechatHelper.getInstance().setCurrentuser(user);
                    long costTime = System.currentTimeMillis() - start;
                    //wait
                    if (sleepTime - costTime > 0) {
                        try {
                            Thread.sleep(sleepTime - costTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //enter main screen
                    startActivity(new Intent(SplashActivity.this, GuideActivity.class));
                    finish();
                } else {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                    }
                    startActivity(new Intent(SplashActivity.this, GuideActivity.class));
                    finish();
                }
            }
        }).start();
    }
}
