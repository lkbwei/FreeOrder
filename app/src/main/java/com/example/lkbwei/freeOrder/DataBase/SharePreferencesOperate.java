package com.example.lkbwei.freeOrder.DataBase;

import android.content.Context;

/**
 * Created by lkbwei on 2017/3/5.
 */

public class SharePreferencesOperate {

    public static void login(Context context,String id,String userName, String pwd
            , int identity, boolean hasLogin){
        BasePreferences.setObjectId(context,id);
        BasePreferences.setUserName(context,userName);
        BasePreferences.setUserPwd(context,pwd);
        BasePreferences.setIdentity(context,identity);
        //预留
        BasePreferences.setLoginStatus(context,hasLogin);
    }

    public static void setRestaurant(Context context,String restaurant){
        BasePreferences.setRestaurant(context,restaurant);
    }

    public static void setRecentRestaurant(Context context,String restaurant){
        BasePreferences.setRecentRestaurant(context,restaurant);
    }


}
