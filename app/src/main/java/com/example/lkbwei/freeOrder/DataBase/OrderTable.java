package com.example.lkbwei.freeOrder.DataBase;

import cn.bmob.v3.BmobObject;

/**
 * Created by lkbwei on 2017/3/14.
 */

public class OrderTable extends BmobObject {
    private String Boss;
    private String Restaurant;
    private String Customer;
    private String FoodName;
    private Double Price;
    private String Comment;
    private String ImageUrl1;
    private String ImageUrl2;
    private String ImageUrl3;
    private Integer Status;//0表示未提交的订单，1表示以接单

    public String getRestaurant() {
        return Restaurant;
    }

    public void setRestaurant(String restaurant) {
        Restaurant = restaurant;
    }

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer status) {
        Status = status;
    }

    public String getBoss() {
        return Boss;
    }

    public void setBoss(String boss) {
        Boss = boss;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getCustomer() {
        return Customer;
    }

    public void setCustomer(String customer) {
        Customer = customer;
    }

    public String getFoodName() {
        return FoodName;
    }

    public void setFoodName(String foodName) {
        FoodName = foodName;
    }

    public String getImageUrl1() {
        return ImageUrl1;
    }

    public void setImageUrl1(String imageUrl1) {
        ImageUrl1 = imageUrl1;
    }

    public String getImageUrl2() {
        return ImageUrl2;
    }

    public void setImageUrl2(String imageUrl2) {
        ImageUrl2 = imageUrl2;
    }

    public String getImageUrl3() {
        return ImageUrl3;
    }

    public void setImageUrl3(String imageUrl3) {
        ImageUrl3 = imageUrl3;
    }

    public Double getPrice() {
        return Price;
    }

    public void setPrice(Double price) {
        Price = price;
    }
}
