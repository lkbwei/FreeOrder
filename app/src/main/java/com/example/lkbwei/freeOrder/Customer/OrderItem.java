package com.example.lkbwei.freeOrder.Customer;

import java.io.Serializable;

/**
 * Created by lkbwei on 2017/3/14.
 */

public class OrderItem implements Serializable{
    private String name;
    private String price;
    private String tips;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }
}
