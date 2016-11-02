package cn.ucai.superwechat.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.utils.BundleUtils;

public class GuideActivity extends BaseActivity {

    @Bind(R.id.btn_login)
    Button btnLogin;
    @Bind(R.id.btn_signup)
    Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_login, R.id.btn_signup})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                BundleUtils.intent(GuideActivity.this,LoginActivity.class);
                break;
            case R.id.btn_signup:
                BundleUtils.intent(GuideActivity.this,RegisterActivity.class);
                break;
        }
    }
}
