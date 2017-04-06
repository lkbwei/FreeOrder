package com.example.lkbwei.freeOrder.DataBase;

import cn.bmob.v3.BmobObject;

/**
 * Created by lkbwei on 2017/3/4.
 */

public class LoginTable extends BmobObject {
    private  String userName;
    private  String password;
    private  Integer identity;
    private  String restaurant;
    private String userImageUrl;

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public Integer getIdentity() {
        return identity;
    }

    public void setIdentity(Integer identity) {
        this.identity = identity;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
