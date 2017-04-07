package com.example.lkbwei.freeOrder.DataBase;

import android.content.Context;
import android.preference.PreferenceManager;

import java.util.Set;

/**
 * Created by lkbwei on 2017/3/4.
 */

public class BasePreferences  {

    public static final int BOSS = 1;
    public static final int CUSTOMER = 2;

    private static final String OBJECT_ID = "id";
    private static final String USER_NAME = "name";
    private static final String USER_PWD = "password";
    private static final String HAS_LOGIN = "hasLogined";
    private static final String IDENTITY = "identity";
    private static final String RESTAURANT = "restaurant";
    private static final String RECENT_RESTAURANT = "recentRestaurant";
    private static final String RESTAURANT_LIST = "list";

    public static Set<String> getRestaurantList(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getStringSet(RESTAURANT_LIST,null);
    }

    public static void setRestaurantList(Context context,Set<String> set){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putStringSet(RESTAURANT_LIST,set)
                .apply();
    }

    public static String getRecentRestaurant(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(RECENT_RESTAURANT,null);
    }


    public static String getObjectId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(OBJECT_ID,null);
    }

    public static String getRESTAURANT(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(RESTAURANT,null);
    }

    public static int getIdentity(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(IDENTITY,-1);
    }

    public static boolean getLoginStatus(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(HAS_LOGIN,false);
    }

    public static String getUserName(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(USER_NAME,null);
    }

    public static String getUserPwd(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(USER_PWD,null);

    }

    public static void setIdentity(Context context,int identity){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(IDENTITY,identity)
                .apply();
    }

    public static void setUserName(Context context,String name){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(USER_NAME,name)
                .apply();
    }

    public static void setUserPwd(Context context,String password){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(USER_PWD,password)
                .apply();
    }

    public static void setLoginStatus(Context context,boolean bool){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(HAS_LOGIN,bool)
                .apply();
    }

    public static void setObjectId(Context context,String id){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(OBJECT_ID,id)
                .apply();
    }

    public static void setRestaurant(Context context,String restaurant){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(RESTAURANT,restaurant)
                .apply();
    }

    public static void setRecentRestaurant(Context context,String restaurant){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(RECENT_RESTAURANT,restaurant)
                .apply();
    }


}
