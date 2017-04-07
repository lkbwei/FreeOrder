package com.example.lkbwei.freeOrder.Login;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.lkbwei.freeOrder.DataBase.BasePreferences;
import com.example.lkbwei.freeOrder.R;

import cn.bmob.v3.Bmob;

/**
 * 登录基函数
 * Created by lkbwei on 2017/3/2.
 */

public abstract class BaseLoginActivity extends AppCompatActivity implements LoginFragment.LoginListener,LoginFragment.RegisterListener
        ,RegisterDialogFragment.DoRegisterListener,WelcomeFragment.WelcomeListener {

    public Handler mHandler;
    public static final int BOSS = 1;
    public static final int CUSTOMER = 2;

    protected Fragment fragment;

    private boolean hasLogined;
    private FragmentManager mFragmentManager;
    private String bmob_key = "c71917f4484a697fe557906d66522454";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        Bmob.initialize(this,bmob_key);

        handleMsg();

        initFragment();
    }

    /**
     * 选择界面
     * 根据用户登录历史，选择登录界面还是免登录界面
     * @since 1.0
     */
    private void initFragment(){
        hasLogined = BasePreferences.getLoginStatus(this);

        mFragmentManager = getSupportFragmentManager();
        fragment = mFragmentManager.findFragmentById(R.id.fragment_container);
        if(fragment == null){
            fragment = createFragment(hasLogined);
            mFragmentManager.beginTransaction()
                    .add(R.id.fragment_container,fragment)
                    .commit();
        }else {
            fragment = createFragment(hasLogined);
            mFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container,fragment)
                    .commit();
        }

        if(hasLogined){
            initReceiver();
        }
    }

    /**
     * 选择Fragment
     * 返回WelcomeFragment或者LoginFragment
     * @param bool 是否已经登录
     * @return 返回WelcomeFragment或者LoginFragment
     * @since 1.0
     */
    private Fragment createFragment(boolean bool){
        if(bool){
            return WelcomeFragment.newInstance();
        }else {
           return LoginFragment.newInstance();
        }
    }

    protected abstract void initReceiver();

    protected abstract void handleMsg();

    protected abstract void editRestaurant();

    protected abstract void chooseRestaurant();

    protected abstract boolean checkRestaurant(String restaurant);

}
