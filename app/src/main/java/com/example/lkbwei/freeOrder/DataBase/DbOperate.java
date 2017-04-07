package com.example.lkbwei.freeOrder.DataBase;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.lkbwei.freeOrder.Login.BaseLoginActivity;
import com.example.lkbwei.freeOrder.Login.LoginActivity;
import com.example.lkbwei.freeOrder.Login.LoginFragment;
import com.example.lkbwei.freeOrder.Tools.NetReceiver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by lkbwei on 2017/3/4.
 */

public class DbOperate  {
    private static String[] objectID = new String[1];

    /**
     * 联网登录验证
     * 根据用户输入信息，从数据库中验证信息正确性
     * @param context 上下文
     * @param user 用户名
     * @param pwd 密码
     * @param identity 身份
     * @param handler 传递消息Handler
     * @param what 标记
     * @since 1.0
     */
    public static void login(final Context context, final String user, final String pwd, final int identity,
                             final Handler handler, final int what){
        boolean isOn = NetReceiver.checkNetConn(context);
        if (!isOn){
            LoginFragment.sHandler.obtainMessage(LoginFragment.NET_STATUS).sendToTarget();
        }else {
            BmobQuery<LoginTable> query = new BmobQuery<>();
            query.addWhereEqualTo("userName",user);
            query.findObjects(new FindListener<LoginTable>() {
                @Override
                public void done(List<LoginTable> list, BmobException e) {
                    if (e == null){
                        if (list == null || list.size() == 0){

                            Toast.makeText(context,"用户不存在",Toast.LENGTH_LONG).show();
                            LoginFragment.sHandler.obtainMessage(LoginFragment.LOGIN_NOT_SUCCESS).sendToTarget();
                        }else {
                            LoginTable login = list.get(0);
                            if (login.getPassword().equals(pwd) && login.getIdentity() == identity){

                                Toast.makeText(context,"登录成功",Toast.LENGTH_LONG).show();

                                objectID[0] = login.getObjectId();

                                SharePreferencesOperate.login(context,objectID[0],user,pwd
                                        ,identity,true);

                                handler.sendEmptyMessage(what);
                            }else {
                                Toast.makeText(context,"密码错误",Toast.LENGTH_LONG).show();
                                LoginFragment.sHandler.obtainMessage(LoginFragment.LOGIN_NOT_SUCCESS).sendToTarget();

                            }
                        }
                    }else {
                        LoginFragment.sHandler.obtainMessage(LoginFragment.NET_STATUS).sendToTarget();
                    }

                }
            });
        }

    }

    /**
     * 注册逻辑
     * 根据用户输入信息，完成注册逻辑
     * @param context 上下文
     * @param user 用户名
     * @param pwd 密码
     * @param identity 身份
     * @param handler 传递消息Handler
     * @param what 标记
     * @since 1.0
     */
    public static void doRegister(final Context context, final String user
            , final String pwd, final int identity,final Handler handler,final int what) {
        if (user == null || pwd == null || (pwd.length() < 8) || (user.length() < 3)) {
            Toast.makeText(context,"格式不正确，请重新输入",Toast.LENGTH_LONG).show();
            handler.sendEmptyMessage(what);

        }else {
            Log.i("login","***************" + user);
            BmobQuery<LoginTable> query = new BmobQuery<>();
            query.addWhereEqualTo("userName",user);
            query.findObjects(new FindListener<LoginTable>() {
                @Override
                public void done(List<LoginTable> list, BmobException e) {
                    if(list != null && list.size() != 0){
                        LoginTable loginTable = list.get(0);
                        Toast.makeText(context, "用户已存在",Toast.LENGTH_LONG).show();
                        handler.sendEmptyMessage(what);
                    }else {
                        LoginTable obj = new LoginTable();
                        obj.setIdentity(identity);
                        obj.setPassword(pwd);
                        obj.setUserName(user);
                        obj.setRestaurant("无");
                        obj.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                Toast.makeText(context,"注册成功",Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }
            });
        }
    }

    /**
     * 获取全部餐厅
     * 从数据库中获取全部餐厅名
     * @param handler 传递消息的Handler
     * @param what 标记
     * @since 1.0
     */
    public static void getAllRestaurant(final Handler handler, final int what){
        BmobQuery<LoginTable> query = new BmobQuery<>();
        query.addWhereEqualTo("identity",BaseLoginActivity.BOSS);
        query.findObjects(new FindListener<LoginTable>() {
            @Override
            public void done(List<LoginTable> list, BmobException e) {
                if (e == null && list != null && list.size() != 0) {
                    List<String> restaurantList = new ArrayList<String>();
                    for (int i = 0;i < list.size();i ++){
                        LoginTable table = list.get(i);
                        restaurantList.add(table.getRestaurant());
                    }
                    handler.obtainMessage(what, restaurantList).sendToTarget();
                }
            }
        });
    }

    /**
     * 验证餐厅合法
     * 根据商家注册的餐厅名，从数据库中验证是否有重复
     * @param name 餐厅名
     * @param handler 传递消息的Handler
     * @since 1.0
     */
    public static void checkHaveRestaurant(String name,final Handler handler){
        BmobQuery<LoginTable> table = new BmobQuery<>();

        table.doSQLQuery("select restaurant from LoginTable where userName = '" + name + "'",
                new SQLQueryListener<LoginTable>() {
                    @Override
                    public void done(BmobQueryResult<LoginTable> bmobQueryResult, BmobException e) {
                        if (e == null){
                            List<LoginTable> list = bmobQueryResult.getResults();
                            if (list.get(0).getRestaurant().equals("无")){
                                handler.sendEmptyMessage(
                                        LoginActivity.EDITRESTAURANT);
                            }else {
                               handler.sendEmptyMessage(
                                        LoginActivity.BOSSLOGIN);
                            }

                        }else{
                            Log.i("Restaurant","查询异常");
                        }
                    }
                });
    }

    /**
     * 注册餐厅
     * 往数据库中写入商家餐厅
     * @param context 上下文
     * @param restaurant 餐厅名
     * @param handler 传递消息的Handler
     * @since 1.0
     */
    public static void editRestaurant(final Context context, final String restaurant,
                                      final Handler handler){
        LoginTable table = new LoginTable();
        table.setRestaurant(restaurant);
        table.update(BasePreferences.getObjectId(context), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    handler.sendEmptyMessage(
                            LoginActivity.BOSSLOGIN);
                    SharePreferencesOperate.setRestaurant(context,restaurant);

                }else {
                    Log.i("EditRestaurant","**********************更新失败");
                }
            }
        });
    }

    /**
     * 获取商家餐厅
     * 根据具体商家名获取其对应餐厅名
     * @param boss 商家名
     * @param handler 传递消息的Handler
     * @param what 标记
     * @since 1.0
     */
    public static void getRestaurant(String boss, final Handler handler, final int what){

        BmobQuery<LoginTable> query = new BmobQuery<>();
        query.addWhereEqualTo("userName",boss);
        query.findObjects(new FindListener<LoginTable>() {
            @Override
            public void done(List<LoginTable> list, BmobException e) {

                if (e == null && list != null && list.size() != 0) {
                    LoginTable table = list.get(0);

                    handler.obtainMessage(what, table.getRestaurant()).sendToTarget();
                }
            }
        });
    }

    /**
     * 设置用户头像
     * 根据用户名往数据库中添加用户头像
     * @param user 用户名
     * @param file 图片本地地址
     * @param handler 传递消息的Handler
     * @param what 标记
     * @since 1.0
     */
    public static void setUserImage(final String user, File file, final Handler handler, final int what){
        final BmobFile bmobFile = new BmobFile(file);
        bmobFile.upload(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    BmobQuery<LoginTable> query = new BmobQuery<LoginTable>();
                    query.addWhereEqualTo("userName",user);
                    query.findObjects(new FindListener<LoginTable>() {
                        @Override
                        public void done(List<LoginTable> list, BmobException e) {
                            if (e == null && list != null && list.size() != 0){
                                LoginTable table = list.get(0);
                                table.setValue("userImageUrl",bmobFile.getUrl());
                                table.update(table.getObjectId(), new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null){
                                            handler.obtainMessage(what).sendToTarget();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 获取用户头像
     * 根据用户名往数据库中获取其头像
     * @param user 用户名
     * @param handler 传递消息的Handler
     * @param what 标记
     * @since 1.0
     */
    public static void getUserImage(String user, final Handler handler, final int what){
        BmobQuery<LoginTable> query = new BmobQuery<>();
        query.addWhereEqualTo("userName",user);
        query.findObjects(new FindListener<LoginTable>() {
            @Override
            public void done(List<LoginTable> list, BmobException e) {
                if (e == null ){
                    LoginTable table = list.get(0);
                    if (table.getUserImageUrl() != null){
                        handler.obtainMessage(what,table.getUserImageUrl()).sendToTarget();
                    }else {
                        handler.obtainMessage(what).sendToTarget();
                    }
                }
            }
        });
    }

}
