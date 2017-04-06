package com.example.lkbwei.freeOrder.Presenter;

import android.content.Intent;
import android.os.Handler;

import com.example.lkbwei.freeOrder.DataBase.BasePreferences;
import com.example.lkbwei.freeOrder.DataBase.DbOperate;
import com.example.lkbwei.freeOrder.DataBase.SharePreferencesOperate;
import com.example.lkbwei.freeOrder.Boss.BaseMenuActivity;
import com.example.lkbwei.freeOrder.DataBase.Lab;
import com.example.lkbwei.freeOrder.Login.LoginActivity;
import com.example.lkbwei.freeOrder.Tools.MD5;


/**
 * Created by lkbwei on 2017/3/29.
 */

public class LoginPresenter extends BasePresenter<LoginActivity> {
    protected Lab mLab;

    @Override
    public void attachView(LoginActivity view){
        super.attachView(view);
        mLab = Lab.getLab(mView.getApplicationContext());
    }
    public void login(String user, final String pwd, final int identity, Handler handler,int what) {

        String md5Pwd = MD5.getMD5(pwd);

        DbOperate.login(mView.getApplicationContext(),user,md5Pwd,identity,handler,what);
    }

    public void register() {

        mView.mHandler.sendEmptyMessage(mView.REGISTERDIOLOG);
    }

    public void doRegister(final String user, final String pwd, final int identity,
                           Handler handler,int what) {

        String md5Pwd = MD5.getMD5(pwd);
        DbOperate.doRegister(mView.getApplicationContext(),user,md5Pwd,identity,handler,what);
    }

    //recentBoss存储的是所选餐厅的商家名
    public void saveBeforeLogin(String recentBoss, int identity) {
        if (recentBoss != null) {
            SharePreferencesOperate.setRecentRestaurant(mView.getApplicationContext(), recentBoss);
        }
        Intent intent = BaseMenuActivity.newIntent(mView.getApplicationContext(),
                identity);
        mView.startActivity(intent);
        mView.finish();
    }

    public void getBoss(String restaurant){
        mLab.getBoss("restaurant",restaurant,mView.mHandler,mView.GET_BOSS);
    }

    public void getAllRestaurant(){
        DbOperate.getAllRestaurant(mView.mHandler,mView.CHOOSE_RESTAURANT);
    }

    public void checkHaveRestaurant(Handler handler){
        DbOperate.checkHaveRestaurant(BasePreferences.getUserName(
                mView.getApplicationContext()),handler);
    }

    public void editRestaurant(String restaurant,Handler handler){
        DbOperate.editRestaurant(mView.getApplicationContext(),restaurant,handler);
    }
}
