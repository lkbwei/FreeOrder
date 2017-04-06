package com.example.lkbwei.freeOrder.DataBase;

import cn.bmob.v3.BmobObject;

/**
 * Created by lkbwei on 2017/3/7.
 */

public class GoodsData extends BmobObject {
    private String User;
    private String FoodName;
    private String ImageUrl;
    private Integer SaleVolume;
    private Double Price;
    private Integer Positive;
    private Integer Negative;
    private String Description;
    private Boolean Stock;
    private String classify;
    private Integer CoverNum;

    public Integer getCoverNum() {
        return CoverNum;
    }

    public void setCoverNum(Integer coverNum) {
        CoverNum = coverNum;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getFoodName() {
        return FoodName;
    }

    public void setFoodName(String foodName) {
        FoodName = foodName;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public Integer getNegative() {
        return Negative;
    }

    public void setNegative(Integer negative) {
        Negative = negative;
    }

    public Integer getPositive() {
        return Positive;
    }

    public void setPositive(Integer positive) {
        Positive = positive;
    }

    public Double getPrice() {
        return Price;
    }

    public void setPrice(Double price) {
        Price = price;
    }

    public Integer getSaleVolume() {
        return SaleVolume;
    }

    public void setSaleVolume(Integer saleVolume) {
        SaleVolume = saleVolume;
    }

    public Boolean getStock() {
        return Stock;
    }

    public void setStock(Boolean stock) {
        Stock = stock;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }
}
