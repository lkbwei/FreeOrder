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

    public static final int HAVE_SUBMIT = 0;
    public static final int TAKE_ORDER = 1;
    public static final int HAVE_SOLVE = 2;

    private static OrderTableLab mOrderTableLab;
    private Context mContext;
    private static final String TAG = "orderTable";

    private OrderTableLab(Context context){
        mContext = context.getApplicationContext();
    }

    /**
     * 获取OrderTableLab实例
     * 获取单例OrderTableLab实例
     * @param context 上下文
     * @return OrderTableLab实例
     * @since 1.0
     */
    public static OrderTableLab getOrderTableLab(Context context){
        if (mOrderTableLab == null) {
            mOrderTableLab = new OrderTableLab(context);
        }
        return mOrderTableLab;
    }

    /**
     * 插入数据
     * @param boss 商家名
     * @param restaurant 餐厅名
     * @param customer 用户名
     * @param foodName 食品名
     * @param price 价格
     * @param comment 评论
     * @param url1 评价图片1，可为null
     * @param url2 评价图片2，可为null
     * @param url3 评价图片3，可为null
     * @param status 状态
     * @param handler 传递消息的Handler
     * @param what 标记
     * @since 1.0
     */
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


    /**
     * 更新数据
     * 先找到对应行的ObjectId，再更新
     * @param boss 商家名
     * @param customer 用户名
     * @param status 状态
     * @param handler 传递消息的Handler
     * @param what 标记
     * @param up 参数为true表示订单状态加1，否则减1
     * @since 1.0
     */
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

    /**
     * 更新评论
     * 先找到对应行的ObjectId，再更新
     * @param boss 商家名
     * @param customer 用户名
     * @param foodName 食品名
     * @param comment 评论
     * @param status 状态
     * @param handler 传递消息的Handler
     * @param what 标记
     * @since 1.0
     */
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

    /**
     * 获取商家记录
     * @param boss 商家名
     * @param status 状态
     * @param handler 传递消息的Handler
     * @param what 标记
     * @since 1.0
     */
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

    /**
     * 获取商家已经处理的订单
     * @param boss 商家名
     * @param status 状态
     * @param handler 传递消息的Handler
     * @param what  标记
     * @since 1.0
     */
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

    /**
     * 获取评价信息
     * @param customer 顾客名
     * @param status 订单状态
     * @param handler 传递消息的Handler
     * @param what 标记
     * @since 1.0
     */
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

    /**
     * 获取评论信息
     * @param boss 商家名
     * @param food 食品名
     * @param handler 传递消息的handler
     * @param what 标记
     * @since 1.0
     */
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

    /**
     * 获取全部订单
     * @param customer 顾客名
     * @param handler 传递消息的Handler
     * @param what 标记
     * @since 1.0
     */
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
