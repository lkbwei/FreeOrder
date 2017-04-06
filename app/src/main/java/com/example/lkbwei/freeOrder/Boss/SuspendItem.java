package com.example.lkbwei.freeOrder.Boss;

import com.example.lkbwei.freeOrder.Customer.OrderItem;

import java.util.List;

/**
 * Created by lkbwei on 2017/3/14.
 */

public class SuspendItem {
    private String mCustomer;
    private List<OrderItem> mList;
    private String mDate;

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getCustomer() {
        return mCustomer;
    }

    public void setCustomer(String customer) {
        mCustomer = customer;
    }

    public List<OrderItem> getList() {
        return mList;
    }

    public void setList(List<OrderItem> list) {
        mList = list;
    }
}
