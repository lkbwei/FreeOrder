package com.example.lkbwei.freeOrder.Tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import com.example.lkbwei.freeOrder.Login.BaseLoginActivity;
import com.example.lkbwei.freeOrder.Login.LoginActivity;

/**
 * Created by lkbwei on 2017/3/5.
 */

public class NetReceiver extends BroadcastReceiver {
    private Handler mHandler;

    public NetReceiver(){

    }

    public NetReceiver(Handler handler){
        mHandler = handler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if ( activeNetInfo != null ) {
            if (activeNetInfo.isConnected()) {
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(LoginActivity.NET_ON);
                }
            }
        }
    }

    public static boolean checkNetConn(Context context){
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (manager == null){
            return false;
        }else {
            Network[] netWorks = manager.getAllNetworks();
            if (netWorks != null && netWorks.length > 0){
                for(int i = 0 ;i < netWorks.length; i++){
                    NetworkInfo info = manager.getNetworkInfo(netWorks[i]);
                    if (info.getState() == NetworkInfo.State.CONNECTED){
                        return true;
                    }
                }

            }
        }

        return false;

    }

}
