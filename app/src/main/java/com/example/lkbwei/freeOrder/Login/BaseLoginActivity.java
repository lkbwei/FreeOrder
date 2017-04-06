package com.example.lkbwei.freeOrder.Login;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.lkbwei.freeOrder.DataBase.BasePreferences;
import com.example.lkbwei.freeOrder.R;

import cn.bmob.v3.Bmob;

/**
 * Created by lkbwei on 2017/3/2.
 */

public abstract class BaseLoginActivity extends AppCompatActivity implements LoginFragment.LoginListener,LoginFragment.RegisterListener
        ,RegisterDialogFragment.DoRegisterListener,WelcomeFragment.WelcomeListener {

    private String bmob_key = "c71917f4484a697fe557906d66522454";

    public static final int BOSS = 1;
    public static final int CUSTOMER = 2;

    private boolean hasLogined;
    private FragmentManager mFragmentManager;
    public Handler mHandler;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        Bmob.initialize(this,bmob_key);

        handleMsg();

        initFragment();
    }


    private void initFragment(){
        hasLogined = BasePreferences.getLoginStatus(this);

        mFragmentManager = getSupportFragmentManager();
        Fragment fragment = mFragmentManager.findFragmentById(R.id.fragment_container);
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

    }

    private Fragment createFragment(boolean bool){
        if(bool){
            initReceiver();
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
