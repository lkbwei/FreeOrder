package com.example.lkbwei.freeOrder.Tools;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.lkbwei.freeOrder.Login.LoginActivity;
import com.example.lkbwei.freeOrder.R;

import org.json.JSONObject;

import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.helper.NotificationCompat;
import cn.bmob.v3.listener.ValueEventListener;

/**
 * Created by lkbwei on 2017/3/15.
 */

public class PollService extends Service {
    private static final String TAG = "PollService";
    private static String customer = "";
    private Thread thread;
    private BmobRealTimeData rtd;

    public static Intent newIntent(Context context){
        Intent intent = new Intent(context,PollService.class);
        return intent;
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        doMyJob(intent);
        return START_STICKY;
    }

    /**
     * 监听是否有新订单
     * @param intent Intent
     * @since 1.0
     */
    private void doMyJob(Intent intent){
        thread = new Thread(){
            @Override
            public void run(){
                Looper.prepare();

                 rtd = new BmobRealTimeData();
                rtd.start(new ValueEventListener() {
                    @Override
                    public void onConnectCompleted(Exception e) {

                        if (rtd.isConnected()){
                            rtd.subTableUpdate("OrderTable");
                        }
                    }

                    @Override
                    public void onDataChange(JSONObject jsonObject) {

                        if (BmobRealTimeData.ACTION_UPDATETABLE.equals(
                                jsonObject.optString("action"))){
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (!data.optString("Customer").equals(customer)){
                                createNotification(PollService.this,"您有新的订单待处理",
                                        data.optString("Customer"));
                                customer = data.optString("Customer");

                            }

                        }

                    }
                });
                Looper.loop();
            }
        };

        thread.start();

    }

    @Override
    public void onDestroy(){
        rtd.unsubTableUpdate("OrderTable");
        super.onDestroy();
        Log.i("DESTROY","已销毁");
    }

    /**
     * 创建通知
     * @param context 上下文
     * @param title 标题
     * @param content 内容
     * @since 1.0
     */
    public void createNotification(Context context,String title,String content){
        Intent intent = new Intent(context, LoginActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context,0,intent,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText("客户：" + content)
                .setAutoCancel(true)
                .setContentIntent(pi)
                .build();
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);
        notificationManager.notify(0,notification);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
