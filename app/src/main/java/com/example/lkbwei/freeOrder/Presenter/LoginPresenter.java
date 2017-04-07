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

    /**
     * 绑定视图
     * @param view 视图
     * @since 1.0
     */
    @Override
    public void attachView(LoginActivity view){
        super.attachView(view);
        mLab = Lab.getLab(mView.getApplicationContext());
    }

    /**
     * 登录
     * @param user 用户名
     * @param pwd 密码
     * @param identity 身份
     * @param handler 传递消息Handler
     * @param what 标记
     * @since 1.0
     */
    public void login(String user, final String pwd, final int identity, Handler handler,int what) {

        String md5Pwd = MD5.getMD5(pwd);

        DbOperate.login(mView.getApplicationContext(),user,md5Pwd,identity,handler,what);
    }

    /**
     * 注册窗口
     * @since 1.0
     */
    public void register() {

        mView.mHandler.sendEmptyMessage(mView.REGISTERDIOLOG);
    }

    /**
     * 注册
     * @param user 用户名
     * @param pwd 密码
     * @param identity 身份
     * @param handler 传递消息的Handler
     * @param what 标记
     * @since 1.0
     */
    public void doRegister(final String user, final String pwd, final int identity,
                           Handler handler,int what) {

        String md5Pwd = MD5.getMD5(pwd);
        DbOperate.doRegister(mView.getApplicationContext(),user,md5Pwd,identity,handler,what);
    }

    /**
     * 存储进SharePreference
     * @param recentBoss 存储的是所选餐厅的商家名
     * @param identity 身份
     * @since 1.0
     */
    public void saveBeforeLogin(String recentBoss, int identity) {
        if (recentBoss != null) {
            SharePreferencesOperate.setRecentRestaurant(mView.getApplicationContext(), recentBoss);
        }
        Intent intent = BaseMenuActivity.newIntent(mView.getApplicationContext(),
                identity);
        mView.startActivity(intent);
        mView.finish();
    }

    /**
     * 获取商家名
     * @param restaurant 餐厅名
     * @since 1.0
     */
    public void getBoss(String restaurant){
        mLab.getBoss("restaurant",restaurant,mView.mHandler,mView.GET_BOSS);
    }

    /**
     * 获取全部餐厅
     * @since 1.0
     */
    public void getAllRestaurant(){
        DbOperate.getAllRestaurant(mView.mHandler,mView.CHOOSE_RESTAURANT);
    }

    /**
     * 验证餐厅
     * @param handler 传递消息的Handler
     * @since 1.0
     */
    public void checkHaveRestaurant(Handler handler){
        DbOperate.checkHaveRestaurant(BasePreferences.getUserName(
                mView.getApplicationContext()),handler);
    }

    /**
     * 注册餐厅
     * @param restaurant 餐厅名
     * @param handler 传递消息的Handler
     * @since 1.0
     */
    public void editRestaurant(String restaurant,Handler handler){
        DbOperate.editRestaurant(mView.getApplicationContext(),restaurant,handler);
    }
}
