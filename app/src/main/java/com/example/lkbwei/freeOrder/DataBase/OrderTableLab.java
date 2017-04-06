package com.example.lkbwei.freeOrder.DataBase;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by lkbwei on 2017/3/14.
 */

public class OrderTableLab {
    private static OrderTableLab mOrderTableLab;
    private Context mContext;

    private static final String TAG = "orderTable";

    public static final int HAVE_SUBMIT = 0;
    public static final int TAKE_ORDER = 1;
    public static final int HAVE_SOLVE = 2;

    private OrderTableLab(Context context){
        mContext = context.getApplicationContext();
    }

    public static OrderTableLab getmOrderTableLab(Context context){
        if (mOrderTableLab == null) {
            mOrderTableLab = new OrderTableLab(context);
        }
        return mOrderTableLab;
    }


    public void insertData(String[] boss,String[] restaurant, String[] customer, String[] foodName, Double[] price, String[] comment,
                            String[] url1, String[] url2, String[] url3, Integer[] status,
                            final Handler handler, final int what){
        List<BmobObject> list = new ArrayList<>();
        for (int i = 0;i < boss.length;i ++){
            OrderTable table = new OrderTable();
            table.setBoss(boss[i]);
            table.setRestaurant(restaurant[i]);
            table.setCustomer(customer[i]);
            table.setComment(comment[i]);
            table.setFoodName(foodName[i]);
            table.setPrice(price[i]);
            table.setComment(comment[i]);
            table.setImageUrl1(url1[i]);
            table.setImageUrl2(url2[i]);
            table.setImageUrl3(url3[i]);
            table.setStatus(status[i]);
            list.add(table);
        }

        new BmobBatch().insertBatch(list).doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> list, BmobException e) {
                if (e == null){
                    handler.obtainMessage(what).sendToTarget();
                }else {
                    Log.e(TAG,e.toString());
                }
            }
        });
    }

    //up参数为true表示订单状态加1，否则减1。
    public void updateData(String boss, String customer, final Integer status, final Handler handler, final int what, final boolean up){
        BmobQuery<OrderTable> query = new BmobQuery<>();
        query.addWhereEqualTo("Boss",boss);
        if (customer != null) {
            query.addWhereEqualTo("Customer", customer);
        }
        query.addWhereEqualTo("Status",status);
        query.findObjects(new FindListener<OrderTable>() {
            @Override
            public void done(List<OrderTable> list, BmobException e) {
                if (e == null && list != null && list.size() != 0){
                    for (int i = 0;i < list.size();i ++){
                        final OrderTable table = list.get(i);
                        if (up){
                            table.setValue("Status",status + 1);
                        }else {
                            table.setValue("Status",status - 1);
                        }

                        table.update(table.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e != null){
                                    Log.e(TAG,"更新订单状态失败" + e.toString());
                                }
                            }
                        });
                    }
                    handler.obtainMessage(what).sendToTarget();
                }
            }
        });
    }


    public void insertComment(String boss, String customer, String foodName, final String comment,int status,
                               final Handler handler, final int what){
        BmobQuery<OrderTable> query = new BmobQuery<>();
        query.addWhereEqualTo("Boss",boss);
        query.addWhereEqualTo("Customer",customer);
        query.addWhereEqualTo("FoodName",foodName);
        query.addWhereEqualTo("Status",status);

        query.findObjects(new FindListener<OrderTable>() {
            @Override
            public void done(List<OrderTable> list, BmobException e) {
                if (e == null && list != null && list.size() != 0){
                    for (int i = 0;i < list.size();i ++){
                        OrderTable table = list.get(i);
                        if (table.getComment().equals("")){
                            table.setValue("Comment",comment);
                            table.increment("Status");
                            table.update(table.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null){
                                        handler.obtainMessage(what).sendToTarget();
                                    }else {
                                        Log.e(TAG,"更新评论出错" + e.toString());
                                    }
                                }
                            });
                            break;
                        }
                    }
                }
            }
        });
    }

    public void getCustomerHistory(String customer, final Handler handler, final int what){
        BmobQuery<OrderTable> query = new BmobQuery<>();
        query.addWhereEqualTo("Customer",customer);
        query.findObjects(new FindListener<OrderTable>() {
            @Override
            public void done(List<OrderTable> list, BmobException e) {
                if (e == null && list != null){
                    handler.obtainMessage(what,list).sendToTarget();
                }else {
                    Log.e(TAG,"获得数据失败" + e.toString());
                }
            }
        });
    }

    public void getCustomerData(String customer, int status, Date date, final Handler handler, final int what){
        BmobQuery<OrderTable> query = new BmobQuery<>();
        query.addWhereEqualTo("Customer",customer);
        query.addWhereEqualTo("Status",status);
        query.findObjects(new FindListener<OrderTable>() {
            @Override
            public void done(List<OrderTable> list, BmobException e) {
                if (e == null && list != null){
                    handler.obtainMessage(what,list).sendToTarget();
                }else {
                    Log.e(TAG,"获得数据失败" + e.toString());
                }
            }
        });
    }

    public void getBossData(String boss, int status,final Handler handler, final int what){
        BmobQuery<OrderTable> query = new BmobQuery<>();
        query.addWhereEqualTo("Boss",boss);
        query.addWhereEqualTo("Status",status);
        query.findObjects(new FindListener<OrderTable>() {
            @Override
            public void done(List<OrderTable> list, BmobException e) {
                if (e == null && list != null){
                    handler.obtainMessage(what,list).sendToTarget();
                }else {
                    Log.e(TAG,"获得数据失败" + e.toString());
                }
            }
        });
    }

    public void getBossHaveSolvedOrder(String boss,int status,final Handler handler,final int what){
        BmobQuery<OrderTable> query = new BmobQuery<>();
        query.addWhereEqualTo("Boss",boss);
        query.addWhereGreaterThanOrEqualTo("Status",status);
        query.findObjects(new FindListener<OrderTable>() {
            @Override
            public void done(List<OrderTable> list, BmobException e) {
                if (e == null && list != null){
                    handler.obtainMessage(what,list).sendToTarget();
                }else {
                    Log.e(TAG,"获得数据失败" + e.toString());
                }
            }
        });
    }

    public void getEvaluateData(String customer, int status, final Handler handler, final int what){
        BmobQuery<OrderTable> query = new BmobQuery<>();
        query.addWhereEqualTo("Customer",customer);
        query.addWhereEqualTo("Status",status);

        query.findObjects(new FindListener<OrderTable>() {
            @Override
            public void done(List<OrderTable> list, BmobException e) {
                if (e == null && list != null){
                    handler.obtainMessage(what,list).sendToTarget();
                }else {
                    Log.e(TAG,"获取为评论信息失败" + e.toString());
                }
            }
        });
    }

    public void getComment(String boss, String food, final Handler handler, final int what){
        BmobQuery<OrderTable> query = new BmobQuery<>();
        query.addWhereEqualTo("Boss",boss);
        query.addWhereEqualTo("FoodName",food);
        query.addWhereNotEqualTo("Comment","");
        query.findObjects(new FindListener<OrderTable>() {
            @Override
            public void done(List<OrderTable> list, BmobException e) {
                if (e == null && list != null && list.size() != 0){
                    handler.obtainMessage(what,list).sendToTarget();
                }else {
                    Log.e(TAG,"获取评论数据失败" + e.toString());
                }
            }
        });
    }

    public void getAllOrder(String customer, final Handler handler, final int what){
        BmobQuery<OrderTable> query = new BmobQuery<>();
        query.addWhereEqualTo("Customer",customer);
        query.order("createdAt");
        query.findObjects(new FindListener<OrderTable>() {
            @Override
            public void done(List<OrderTable> list, BmobException e) {
                if (e == null && list != null){
                    handler.obtainMessage(what,list).sendToTarget();
                }
            }
        });

    }

}
